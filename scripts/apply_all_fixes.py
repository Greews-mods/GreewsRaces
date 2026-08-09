import json
import re
import shutil
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
PORT = Path(r"c:\Users\farda\Desktop\_greewsraces_icon_port")
MOD_VERSION = "1.2.1-Beta"

BRANCHES = [
    ("main", "modern", "modern_lang", None, "sided"),
    ("mc-1.21.10", "modern", "modern_lang", None, "sided"),
    ("mc-1.21.1", "legacy", "modern_lang", None, "legacy"),
    ("mc-1.20.1", "legacy", "legacy_lang", "JAVA_17", "legacy"),
]

DRAW_BLOCKS = {
    "modern": """    private void drawRaceIcon(DrawContext context, int x, int y, int size, Race race) {
        RaceIcon icon = AllRaceIcons.byRace(race);
        context.drawTexturedQuad(
            icon.getTexture(),
            x, y, x + size, y + size,
            0.0F, 1.0F, 0.0F, 1.0F
        );
    }""",
    "legacy": """    private void drawRaceIcon(DrawContext context, int x, int y, int size, Race race) {
        RaceIcon icon = AllRaceIcons.byRace(race);
        int texSize = icon.getSize();
        context.drawTexture(
            icon.getTexture(),
            x, y,
            size, size,
            0.0F, 0.0F,
            texSize, texSize,
            texSize, texSize
        );
    }""",
}

ICON_PATTERN = re.compile(
    r"    private void drawRaceIcon\(DrawContext context, int x, int y, int size, Race race\) \{.*?\n    \}\n"
    r"(?:    private void drawFallbackIcon\(DrawContext context, int x, int y, int size, Race race\) \{.*?\n    \}\n)?",
    re.DOTALL,
)

RACE_CMD_PATTERNS = [
    (
        '.requires(RaceCommand::hasPermissionLevel2)\n                .executes(RaceCommand::openRaceMenuSelf)\n                .then(CommandManager.argument("player", EntityArgumentType.player())\n                    .executes(RaceCommand::openRaceMenuTarget))',
        '.executes(RaceCommand::openRaceMenuSelf)\n                .then(CommandManager.argument("player", EntityArgumentType.player())\n                    .requires(RaceCommand::hasPermissionLevel2)\n                    .executes(RaceCommand::openRaceMenuTarget))',
    ),
    (
        '.requires(source -> source.hasPermissionLevel(2))\n                .executes(RaceCommand::openRaceMenuSelf)\n                .then(CommandManager.argument("player", EntityArgumentType.player())\n                    .executes(RaceCommand::openRaceMenuTarget))',
        '.executes(RaceCommand::openRaceMenuSelf)\n                .then(CommandManager.argument("player", EntityArgumentType.player())\n                    .requires(source -> source.hasPermissionLevel(2))\n                    .executes(RaceCommand::openRaceMenuTarget))',
    ),
]

MIXIN_TARGETS = {
    "sided": (
        "Lnet/minecraft/entity/Entity;sidedDamage(Lnet/minecraft/entity/damage/DamageSource;F)Z",
        1,
    ),
    "legacy": (
        "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z",
        1,
    ),
}

MODRINTH_GRADLE = """
def modrinthProfileNames = [
    '1.21.11': 'testing 1.21.11',
    '1.21.10': 'testing',
    '1.21.1' : 'testing 1.21.1',
    '1.20.1' : 'testing 1.20.1',
]

tasks.register('copyToModrinthTesting') {
    dependsOn remapJar
    doLast {
        def profileName = modrinthProfileNames[minecraft_version]
        if (profileName == null) {
            return
        }
        def modsDir = file("${System.getProperty('user.home')}/AppData/Roaming/ModrinthApp/profiles/${profileName}/mods")
        modsDir.mkdirs()
        modsDir.listFiles()?.each { candidate ->
            if (candidate.name.startsWith('greewsraces') && candidate.name.endsWith('.jar')) {
                candidate.delete()
            }
        }
        def outJar = layout.buildDirectory.file("libs/${archives_base_name}-${version}.jar").get().asFile
        copy {
            from outJar
            into modsDir
        }
        logger.lifecycle("Copied ${outJar.name} to ${modsDir}")
    }
}

build.finalizedBy copyToModrinthTesting
"""


def install_icons() -> None:
    subprocess.run([sys.executable, str(ROOT / "scripts" / "install_race_icons.py")], cwd=ROOT, check=True)
    port_assets = PORT / "assets" / "greewsraces"
    if port_assets.exists():
        shutil.rmtree(port_assets)
    shutil.copytree(
        ROOT / "src/main/resources/assets/greewsraces",
        port_assets,
    )


def patch_race_command(path: Path) -> None:
    text = path.read_text(encoding="utf-8")
    for old, new in RACE_CMD_PATTERNS:
        if old in text:
            path.write_text(text.replace(old, new, 1), encoding="utf-8", newline="\n")
            return
    if ".requires(RaceCommand::hasPermissionLevel2)" not in text and "hasPermissionLevel(2)" not in text.split("openRaceMenuSelf")[0]:
        return
    raise RuntimeError(f"RaceCommand patch failed for {path}")


def patch_player_mixin(path: Path, kind: str) -> None:
    target, index = MIXIN_TARGETS[kind]
    text = path.read_text(encoding="utf-8")
    text, count = re.subn(
        r"target = \"Lnet/minecraft/entity/[^\"]+;(?:damage|sidedDamage)\([^\)]+\)Z\"\s*\n\s*\),\s*\n\s*index = \d+",
        f"target = \"{target}\"\n        ),\n        index = {index}",
        text,
        count=1,
    )
    if count != 1:
        raise RuntimeError(f"PlayerEntityMixin patch failed for {path}")
    path.write_text(text, encoding="utf-8", newline="\n")


def patch_build_gradle(path: Path) -> None:
    text = path.read_text(encoding="utf-8")
    if "copyToModrinthTesting" in text:
        return
    if "build.finalizedBy copyToMcBuilds" not in text:
        raise RuntimeError("build.gradle missing copyToMcBuilds hook")
    text = text.replace("build.finalizedBy copyToMcBuilds", "build.finalizedBy copyToMcBuilds" + MODRINTH_GRADLE)
    path.write_text(text, encoding="utf-8", newline="\n")


def patch_greews_client(path: Path) -> None:
    text = path.read_text(encoding="utf-8")
    if "calculateDimensions();" in text:
        return

    if "import net.minecraft.entity.player.PlayerEntity;" not in text:
        text = text.replace(
            "import net.minecraft.client.MinecraftClient;\n",
            "import net.minecraft.client.MinecraftClient;\nimport net.minecraft.entity.player.PlayerEntity;\n",
            1,
        )

    if "(payload, context)" in text:
        text = text.replace(
            "                if (client.player != null) {\n                    ClientRaceStorage.setRace(client.player.getUuid(), clientRaceId);\n                }\n",
            "                if (client.player != null) {\n                    ClientRaceStorage.setRace(client.player.getUuid(), clientRaceId);\n                    client.player.calculateDimensions();\n                }\n",
            1,
        )
        text = text.replace(
            "                ClientRaceStorage.setRace(payload.playerId(), payload.raceId());\n                GreewsRaces.LOGGER.info(\"Received race sync for player {}: {}\",\n                    payload.playerId(), payload.raceId());\n",
            "                ClientRaceStorage.setRace(payload.playerId(), payload.raceId());\n                MinecraftClient client = context.client();\n                if (client.world != null) {\n                    PlayerEntity syncedPlayer = client.world.getPlayerByUuid(payload.playerId());\n                    if (syncedPlayer != null) {\n                        syncedPlayer.calculateDimensions();\n                    }\n                }\n                GreewsRaces.LOGGER.info(\"Received race sync for player {}: {}\",\n                    payload.playerId(), payload.raceId());\n",
            1,
        )
    else:
        text = text.replace(
            "                if (c.player != null) {\n                    ClientRaceStorage.setRace(c.player.getUuid(), clientRaceId);\n                }\n",
            "                if (c.player != null) {\n                    ClientRaceStorage.setRace(c.player.getUuid(), clientRaceId);\n                    c.player.calculateDimensions();\n                }\n",
            1,
        )
        text = text.replace(
            "                ClientRaceStorage.setRace(payload.playerId(), payload.raceId());\n                GreewsRaces.LOGGER.info(\"Received race sync for player {}: {}\",\n                    payload.playerId(), payload.raceId());\n",
            "                ClientRaceStorage.setRace(payload.playerId(), payload.raceId());\n                if (client.world != null) {\n                    PlayerEntity syncedPlayer = client.world.getPlayerByUuid(payload.playerId());\n                    if (syncedPlayer != null) {\n                        syncedPlayer.calculateDimensions();\n                    }\n                }\n                GreewsRaces.LOGGER.info(\"Received race sync for player {}: {}\",\n                    payload.playerId(), payload.raceId());\n",
            1,
        )

    path.write_text(text, encoding="utf-8", newline="\n")


def patch_1201_race_handler(path: Path) -> None:
    text = path.read_text(encoding="utf-8")
    if "calculateDimensions();" in text:
        return
    text = text.replace(
        "        if (player.getHealth() > player.getMaxHealth()) {\n            player.setHealth(player.getMaxHealth());\n        }\n    }\n\n    public static void resetAttributes",
        "        if (player.getHealth() > player.getMaxHealth()) {\n            player.setHealth(player.getMaxHealth());\n        }\n\n        player.calculateDimensions();\n    }\n\n    public static void resetAttributes",
        1,
    )
    text = text.replace(
        "        if (player.getHealth() > player.getMaxHealth()) {\n            player.setHealth(player.getMaxHealth());\n        }\n    }\n}",
        "        if (player.getHealth() > player.getMaxHealth()) {\n            player.setHealth(player.getMaxHealth());\n        }\n\n        player.calculateDimensions();\n    }\n}",
        1,
    )
    path.write_text(text, encoding="utf-8", newline="\n")


def apply_branch(branch: str, icon_key: str, lang_key: str, mixin_level: str | None, mixin_kind: str) -> None:
    subprocess.run(["git", "checkout", "-f", branch], cwd=ROOT, check=True)
    java = ROOT / "src/main/java/com/greewsraces"
    shutil.copy2(PORT / "RaceIcon.java", java / "RaceIcon.java")
    shutil.copy2(PORT / "AllRaceIcons.java", java / "AllRaceIcons.java")
    lang_src = PORT / (
        "LanguageSelectionScreen_modern.java"
        if lang_key == "modern_lang"
        else "LanguageSelectionScreen_1_20_1.java"
    )
    shutil.copy2(lang_src, java / "LanguageSelectionScreen.java")
    assets_dst = ROOT / "src/main/resources/assets/greewsraces"
    if assets_dst.exists():
        shutil.rmtree(assets_dst)
    shutil.copytree(PORT / "assets/greewsraces", assets_dst)

    screen = java / "RaceSelectionScreen.java"
    text = screen.read_text(encoding="utf-8")
    new_text, count = ICON_PATTERN.subn(DRAW_BLOCKS[icon_key] + "\n", text, count=1)
    if count != 1:
        raise RuntimeError(f"{branch}: icon patch failed")
    screen.write_text(new_text, encoding="utf-8", newline="\n")

    patch_race_command(java / "RaceCommand.java")
    patch_player_mixin(java / "mixin/PlayerEntityMixin.java", mixin_kind)
    patch_build_gradle(ROOT / "build.gradle")
    patch_greews_client(java / "GreewsRacesClient.java")

    if branch == "mc-1.20.1":
        shutil.copy2(PORT / "EntityScaleMixin.java", java / "mixin/EntityScaleMixin.java")
        shutil.copy2(PORT / "PlayerRendererScaleMixin.java", java / "mixin/PlayerRendererScaleMixin.java")
        patch_1201_race_handler(java / "RaceHandler.java")

    props = ROOT / "gradle.properties"
    props.write_text(
        re.sub(r"^mod_version=.*$", f"mod_version={MOD_VERSION}", props.read_text(encoding="utf-8"), flags=re.M),
        encoding="utf-8",
    )

    mixins = ROOT / "src/main/resources/greewsraces.mixins.json"
    data = json.loads(mixins.read_text(encoding="utf-8"))
    if mixin_level:
        data["compatibilityLevel"] = mixin_level
    mixins_list = data.get("mixins", [])
    if branch == "mc-1.20.1" and "EntityScaleMixin" not in mixins_list:
        mixins_list.insert(2, "EntityScaleMixin")
        data["mixins"] = mixins_list
    client_mixins = data.get("client", [])
    if branch == "mc-1.20.1" and "PlayerRendererScaleMixin" not in client_mixins:
        client_mixins.append("PlayerRendererScaleMixin")
        data["client"] = client_mixins
    mixins.write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8")

    print(f"Building {branch}...")
    result = subprocess.run([".\\gradlew.bat", "clean", "build", "--no-daemon"], cwd=ROOT)
    if result.returncode != 0:
        raise SystemExit(f"Build failed: {branch}")
    print(f"OK {branch}")


def main() -> None:
    install_icons()
    only = sys.argv[1] if len(sys.argv) > 1 else None
    for cfg in BRANCHES:
        if only and cfg[0] != only:
            continue
        apply_branch(*cfg)
    print("ALL DONE")


if __name__ == "__main__":
    main()

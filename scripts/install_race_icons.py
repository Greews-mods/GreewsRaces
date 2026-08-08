from pathlib import Path

from PIL import Image

ASSETS = Path(
    r"C:\Users\farda\.cursor\projects\c-Users-farda-Desktop-GreewsRaces\assets"
)
OUT = Path(__file__).resolve().parents[1] / "src" / "main" / "resources" / "assets" / "greewsraces"
ICON_SIZE = 128

RACE_SOURCES = {
    "human": "c__Users_farda_AppData_Roaming_Cursor_User_workspaceStorage_empty-window_images_human-a894608c-8e5b-485b-a267-2dda45e21efd.png",
    "wood_elf": "c__Users_farda_AppData_Roaming_Cursor_User_workspaceStorage_empty-window_images_wood_elf-67e75d9f-a5e5-4904-8358-3ed64c8e94e3.png",
    "night_elf": "c__Users_farda_AppData_Roaming_Cursor_User_workspaceStorage_empty-window_images_night_elf-abbc657a-3c63-4fd3-a0f4-a87fdf503114.png",
    "dwarf": "c__Users_farda_AppData_Roaming_Cursor_User_workspaceStorage_empty-window_images_dwarf-ea5ae15b-3563-4702-9854-9822cedd3ab0.png",
    "demon": "c__Users_farda_AppData_Roaming_Cursor_User_workspaceStorage_empty-window_images_demon-641bd119-ebcf-43ae-8a1e-5178540ddf15.png",
    "vampire": "c__Users_farda_AppData_Roaming_Cursor_User_workspaceStorage_empty-window_images_vampire-65604b52-1d63-41cb-91c4-1c6c5ea55f8f.png",
    "ghoul": "c__Users_farda_AppData_Roaming_Cursor_User_workspaceStorage_empty-window_images_ghoul-55de1183-c2ed-4b8f-ae56-0414a2382d69.png",
    "fairy": "c__Users_farda_AppData_Roaming_Cursor_User_workspaceStorage_empty-window_images_fairy-07ae4cd1-48d8-4c8e-a227-af14c575315a.png",
}


def trim_built_in_frame(image: Image.Image) -> Image.Image:
    """Remove the grey portrait frame baked into the source icons."""
    width, height = image.size
    pixels = image.load()
    corner = pixels[0, 0][:3]
    threshold = 42

    def is_border(x: int, y: int) -> bool:
        r, g, b, a = pixels[x, y]
        if a < 200:
            return True
        return (
            abs(r - corner[0]) <= threshold
            and abs(g - corner[1]) <= threshold
            and abs(b - corner[2]) <= threshold
        )

    left = 0
    while left < width and all(is_border(left, y) for y in range(height)):
        left += 1

    right = width - 1
    while right >= left and all(is_border(right, y) for y in range(height)):
        right -= 1

    top = 0
    while top < height and all(is_border(x, top) for x in range(width)):
        top += 1

    bottom = height - 1
    while bottom >= top and all(is_border(x, bottom) for x in range(width)):
        bottom -= 1

    if right <= left or bottom <= top:
        return image

    return image.crop((left, top, right + 1, bottom + 1))


def prepare_icon(source: Path) -> Image.Image:
    image = Image.open(source).convert("RGBA")
    width, height = image.size
    side = min(width, height)
    left = (width - side) // 2
    top = (height - side) // 2
    square = image.crop((left, top, left + side, top + side))
    square = trim_built_in_frame(square)
    return square.resize((ICON_SIZE, ICON_SIZE), Image.Resampling.LANCZOS)


def main() -> None:
    races_dir = OUT / "textures" / "gui" / "races"
    races_dir.mkdir(parents=True, exist_ok=True)

    for race_id, filename in RACE_SOURCES.items():
        source = ASSETS / filename
        if not source.exists():
            raise FileNotFoundError(f"Missing source icon: {source}")

        prepare_icon(source).save(races_dir / f"{race_id}.png")

    human = Image.open(races_dir / "human.png")
    human.resize((128, 128), Image.Resampling.LANCZOS).save(OUT / "icon.png")
    print(f"Installed {len(RACE_SOURCES)} race icons at {ICON_SIZE}x{ICON_SIZE} to {OUT}")


if __name__ == "__main__":
    main()

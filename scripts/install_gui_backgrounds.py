from pathlib import Path

from PIL import Image

ASSETS = Path(
    r"C:\Users\farda\.cursor\projects\c-Users-farda-Desktop-GreewsRaces\assets"
)
OUT = Path(__file__).resolve().parents[1] / "src" / "main" / "resources" / "assets" / "greewsraces" / "textures" / "gui"

BACKGROUNDS = {
    "race_selection_bg.png": "c__Users_farda_AppData_Roaming_Cursor_User_workspaceStorage_empty-window_images_racebg-b3a4a406-8b3a-417b-82cb-2ec7551a7bc7.png",
    "language_selection_bg.png": "c__Users_farda_AppData_Roaming_Cursor_User_workspaceStorage_empty-window_images_langbg-9ad64607-9ff9-49a6-bd43-ee6c856bf050.png",
}


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)

    for filename, source_name in BACKGROUNDS.items():
        source = ASSETS / source_name
        if not source.exists():
            raise FileNotFoundError(f"Missing background source: {source}")

        image = Image.open(source).convert("RGBA")
        image.save(OUT / filename)

    print(f"Installed {len(BACKGROUNDS)} GUI backgrounds to {OUT}")


if __name__ == "__main__":
    main()

from pathlib import Path

from PIL import Image

SOURCE = Path(
    r"C:\Users\farda\.cursor\projects\c-Users-farda-Desktop-GreewsRaces\assets"
    r"\c__Users_farda_AppData_Roaming_Cursor_User_workspaceStorage_empty-window_images_"
    r"Gemini_Generated_Image_r8273br8273br827-8b5c3ea0-e09b-4815-a60c-884cae872dfb.png"
)
OUT = Path(__file__).resolve().parents[1] / "src" / "main" / "resources" / "assets" / "greewsraces"

RACES = [
    ("human", 0, 0),
    ("wood_elf", 1, 0),
    ("night_elf", 2, 0),
    ("dwarf", 3, 0),
    ("demon", 0, 1),
    ("vampire", 1, 1),
    ("ghoul", 2, 1),
    ("fairy", 3, 1),
]

LABEL_HEIGHT = 58
ICON_SIZE = 64


def main() -> None:
    image = Image.open(SOURCE).convert("RGBA")
    width, height = image.size
    cell_w = width // 4
    cell_h = height // 2
    portrait_h = cell_h - LABEL_HEIGHT

    races_dir = OUT / "textures" / "gui" / "races"
    races_dir.mkdir(parents=True, exist_ok=True)

    for race_id, col, row in RACES:
        portrait_top = row * cell_h
        side = min(cell_w, portrait_h)
        left = col * cell_w + (cell_w - side) // 2
        top = portrait_top + (portrait_h - side) // 2
        right = left + side
        bottom = top + side

        crop = image.crop((left, top, right, bottom))
        crop = crop.resize((ICON_SIZE, ICON_SIZE), Image.Resampling.LANCZOS)
        crop.save(races_dir / f"{race_id}.png")

    human = Image.open(races_dir / "human.png")
    mod_icon = human.resize((128, 128), Image.Resampling.LANCZOS)
    mod_icon.save(OUT / "icon.png")
    print(f"Wrote race icons to {OUT}")


if __name__ == "__main__":
    main()

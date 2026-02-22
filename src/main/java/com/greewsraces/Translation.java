package com.greewsraces;

import java.util.HashMap;
import java.util.Map;

public class Translation {
    private static final Map<String, Map<String, String>> translations = new HashMap<>();

    static {

        // ==================== ČEŠTINA ====================
        Map<String, String> cs = new HashMap<>();
        cs.put("select_race", "Vyberte si rasu");
        cs.put("select", "Vybrat");
        cs.put("bonuses", "Bonusy:");
        cs.put("maluses", "Nevýhody:");

        cs.put("race.human", "Člověk");
        cs.put("race.human.desc", "Všestranný bojovník");
        cs.put("race.human.bonus", "+20% poškození s mečem");
        cs.put("race.human.malus", "Žádné specifické nevýhody");

        cs.put("race.dwarf", "Trpaslík");
        cs.put("race.dwarf.desc", "Mistr sekery a podzemí");
        cs.put("race.dwarf.bonus", "+20% poškození se sekerou\n+20% rychlost těžení\n+2 brnění\n+20% dosah");
        cs.put("race.dwarf.malus", "-20% poškození s mečem\n-20% poškození s lukem\n-10% rychlost pohybu\n-20% výška");

        cs.put("race.night_elf", "Noční Elf");
        cs.put("race.night_elf.desc", "Střelec stínů");
        cs.put("race.night_elf.bonus", "+20% poškození s kuší\n+20% rychlost pohybu v noci");
        cs.put("race.night_elf.malus", "-20% poškození se sekerou\n-20% poškození s lukem\n+20% výška");

        cs.put("race.wood_elf", "Lesní Elf");
        cs.put("race.wood_elf.desc", "Mistr luku a lesa");
        cs.put("race.wood_elf.bonus", "+20% HP\n+20% poškození s lukem\n+10% rychlost pohybu");
        cs.put("race.wood_elf.malus", "-20% poškození se sekerou\n+20% výška");

        cs.put("race.demon", "Démon");
        cs.put("race.demon.desc", "Pán ohně a destrukce");
        cs.put("race.demon.bonus", "+20% poškození s mečem\nImunita vůči ohni a lávě");
        cs.put("race.demon.malus", "-20% poškození s lukem\n-20% poškození s kuší\nPoškození z vody a deště");

        cs.put("race.vampire", "Upír");
        cs.put("race.vampire.desc", "Nestárnoucí lovec krve");
        cs.put("race.vampire.bonus", "+20% poškození se všemi zbraněmi\n30% šance na lifesteal");
        cs.put("race.vampire.malus", "Hoří na slunci");

        cs.put("race.ghoul", "Ghůl");
        cs.put("race.ghoul.desc", "Bytost temnoty a rozkladu");
        cs.put("race.ghoul.bonus", "Může dýchat pod vodou\nHnilobné maso ho nakrmí\nNehoří na slunci");
        cs.put("race.ghoul.malus", "Může jíst pouze syrové maso");

        cs.put("race.fairy", "Víla");
        cs.put("race.fairy.desc", "Lehká bytost přírody");
        cs.put("race.fairy.bonus", "+10% rychlost pohybu\n+20% poškození s tridentem\n+50% brnění z kožené zbroje\n+20% dosah");
        cs.put("race.fairy.malus", "-30% výška\n-20% poškození se sekerou\n-20% poškození s krumpáčem");

        translations.put("cs", cs);


        // ==================== ANGLIČTINA ====================
        Map<String, String> en = new HashMap<>();
        en.put("select_race", "Select your race");
        en.put("select", "Select");
        en.put("bonuses", "Bonuses:");
        en.put("maluses", "Drawbacks:");

        en.put("race.human", "Human");
        en.put("race.human.desc", "Versatile warrior");
        en.put("race.human.bonus", "+20% damage with sword");
        en.put("race.human.malus", "No specific drawbacks");

        en.put("race.dwarf", "Dwarf");
        en.put("race.dwarf.desc", "Master of axe and underground");
        en.put("race.dwarf.bonus", "+20% damage with axe\n+20% mining speed\n+2 armor\n+20% reach");
        en.put("race.dwarf.malus", "-20% damage with sword\n-20% damage with bow\n-10% movement speed\n-20% height");

        en.put("race.night_elf", "Night Elf");
        en.put("race.night_elf.desc", "Shadow archer");
        en.put("race.night_elf.bonus", "+20% damage with crossbow\n+20% movement speed at night");
        en.put("race.night_elf.malus", "-20% damage with axe\n-20% damage with bow\n+20% height");

        en.put("race.wood_elf", "Wood Elf");
        en.put("race.wood_elf.desc", "Master of bow and forest");
        en.put("race.wood_elf.bonus", "+20% HP\n+20% damage with bow\n+10% movement speed");
        en.put("race.wood_elf.malus", "-20% damage with axe\n+20% height");

        en.put("race.demon", "Demon");
        en.put("race.demon.desc", "Lord of fire and destruction");
        en.put("race.demon.bonus", "+20% damage with sword\nImmunity to fire and lava");
        en.put("race.demon.malus", "-20% damage with bow\n-20% damage with crossbow\nDamage from water and rain");

        en.put("race.vampire", "Vampire");
        en.put("race.vampire.desc", "Immortal blood hunter");
        en.put("race.vampire.bonus", "+20% damage with all weapons\n30% chance for lifesteal");
        en.put("race.vampire.malus", "Burns in sunlight");

        en.put("race.ghoul", "Ghoul");
        en.put("race.ghoul.desc", "Creature of darkness and decay");
        en.put("race.ghoul.bonus", "Can breathe underwater\nRotten flesh feeds it\nDoes not burn in sunlight");
        en.put("race.ghoul.malus", "Can only eat raw meat");

        en.put("race.fairy", "Fairy");
        en.put("race.fairy.desc", "Light creature of nature");
        en.put("race.fairy.bonus", "+10% movement speed\n+20% damage with trident\n+50% armor from leather armor\n+20% reach");
        en.put("race.fairy.malus", "-30% height\n-20% damage with axe\n-20% damage with pickaxe");

        translations.put("en", en);


        // ==================== ŠPANĚLŠTINA ====================
        Map<String, String> es = new HashMap<>();
        es.put("select_race", "Selecciona tu raza");
        es.put("select", "Seleccionar");
        es.put("bonuses", "Bonificaciones:");
        es.put("maluses", "Desventajas:");

        es.put("race.human", "Humano");
        es.put("race.human.desc", "Guerrero versátil");
        es.put("race.human.bonus", "+20% de daño con espada");
        es.put("race.human.malus", "Sin desventajas específicas");

        es.put("race.dwarf", "Enano");
        es.put("race.dwarf.desc", "Maestro del hacha y subterráneo");
        es.put("race.dwarf.bonus", "+20% de daño con hacha\n+20% velocidad de minería\n+2 armadura\n+20% alcance");
        es.put("race.dwarf.malus", "-20% de daño con espada\n-20% de daño con arco\n-10% velocidad de movimiento\n-20% altura");

        es.put("race.night_elf", "Elfo Nocturno");
        es.put("race.night_elf.desc", "Arquero de las sombras");
        es.put("race.night_elf.bonus", "+20% de daño con ballesta\n+20% velocidad de movimiento por la noche");
        es.put("race.night_elf.malus", "-20% de daño con hacha\n-20% de daño con arco\n+20% altura");

        es.put("race.wood_elf", "Elfo del Bosque");
        es.put("race.wood_elf.desc", "Maestro del arco y el bosque");
        es.put("race.wood_elf.bonus", "+20% HP\n+20% de daño con arco\n+10% velocidad de movimiento");
        es.put("race.wood_elf.malus", "-20% de daño con hacha\n+20% altura");

        es.put("race.demon", "Demonio");
        es.put("race.demon.desc", "Señor del fuego y la destrucción");
        es.put("race.demon.bonus", "+20% de daño con espada\nInmunidad al fuego y lava");
        es.put("race.demon.malus", "-20% de daño con arco\n-20% de daño con ballesta\nDaño del agua y lluvia");

        es.put("race.vampire", "Vampiro");
        es.put("race.vampire.desc", "Cazador de sangre inmortal");
        es.put("race.vampire.bonus", "+20% de daño con todas las armas\n30% de probabilidad de robo de vida");
        es.put("race.vampire.malus", "Arde bajo la luz del sol");

        es.put("race.ghoul", "Ghoul");
        es.put("race.ghoul.desc", "Criatura de la oscuridad y la decadencia");
        es.put("race.ghoul.bonus", "Puede respirar bajo el agua\nLa carne podrida lo alimenta\nNo arde bajo el sol");
        es.put("race.ghoul.malus", "Solo puede comer carne cruda");

        es.put("race.fairy", "Hada");
        es.put("race.fairy.desc", "Criatura ligera de la naturaleza");
        es.put("race.fairy.bonus", "+10% velocidad de movimiento\n+20% de daño con tridente\n+50% armadura de cuero\n+20% alcance");
        es.put("race.fairy.malus", "-30% altura\n-20% de daño con hacha\n-20% de daño con pico");

        translations.put("es", es);


        // ==================== RUŠTINA ====================
        Map<String, String> ru = new HashMap<>();
        ru.put("select_race", "Выберите расу");
        ru.put("select", "Выбрать");
        ru.put("bonuses", "Бонусы:");
        ru.put("maluses", "Недостатки:");

        ru.put("race.human", "Человек");
        ru.put("race.human.desc", "Универсальный воин");
        ru.put("race.human.bonus", "+20% урона мечом");
        ru.put("race.human.malus", "Нет особых недостатков");

        ru.put("race.dwarf", "Дварф");
        ru.put("race.dwarf.desc", "Мастер топора и подземелий");
        ru.put("race.dwarf.bonus", "+20% урона топором\n+20% скорость добычи\n+2 броня\n+20% дальность");
        ru.put("race.dwarf.malus", "-20% урона мечом\n-20% урона луком\n-10% скорость движения\n-20% рост");

        ru.put("race.night_elf", "Ночной Эльф");
        ru.put("race.night_elf.desc", "Лучник теней");
        ru.put("race.night_elf.bonus", "+20% урона арбалетом\n+20% скорость движения ночью");
        ru.put("race.night_elf.malus", "-20% урона топором\n-20% урона луком\n+20% рост");

        ru.put("race.wood_elf", "Лесной Эльф");
        ru.put("race.wood_elf.desc", "Мастер лука и леса");
        ru.put("race.wood_elf.bonus", "+20% HP\n+20% урона луком\n+10% скорость движения");
        ru.put("race.wood_elf.malus", "-20% урона топором\n+20% рост");

        ru.put("race.demon", "Демон");
        ru.put("race.demon.desc", "Повелитель огня и разрушения");
        ru.put("race.demon.bonus", "+20% урона мечом\nИммунитет к огню и лаве");
        ru.put("race.demon.malus", "-20% урона луком\n-20% урона арбалетом\nУрон от воды и дождя");

        ru.put("race.vampire", "Вампир");
        ru.put("race.vampire.desc", "Бессмертный охотник за кровью");
        ru.put("race.vampire.bonus", "+20% урона всем оружием\n30% шанс вампиризма");
        ru.put("race.vampire.malus", "Горит на солнце");

        ru.put("race.ghoul", "Гуль");
        ru.put("race.ghoul.desc", "Существо тьмы и разложения");
        ru.put("race.ghoul.bonus", "Может дышать под водой\nГнилое мясо его питает\nНе горит на солнце");
        ru.put("race.ghoul.malus", "Может есть только сырое мясо");

        ru.put("race.fairy", "Фея");
        ru.put("race.fairy.desc", "Лёгкое существо природы");
        ru.put("race.fairy.bonus", "+10% скорость движения\n+20% урона трезубцем\n+50% броня от кожаной брони\n+20% дальность");
        ru.put("race.fairy.malus", "-30% рост\n-20% урона топором\n-20% урона киркой");

        translations.put("ru", ru);
    }

    public static String get(String key, Language language) {
        Map<String, String> lang = translations.get(language.getCode());
        return lang != null ? lang.getOrDefault(key, key) : key;
    }
}
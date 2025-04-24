package com.jsb.arhomerenovat.feature_ar_home.presentation.util

import com.jsb.arhomerenovat.R

fun getPngImageForModel(modelFileName: String): Int {
    return when (modelFileName) {
        "android robot.glb" -> R.drawable.android_robot
        "Black Chair.glb" -> R.drawable.black_chair
        "White Chair.glb" -> R.drawable.white_chair
        "Gray Chair.glb" -> R.drawable.gray_chair
        "Brown Table 1.glb" -> R.drawable.brown_table_1
        "Brown Table 2.glb" -> R.drawable.brown_table_2
        "White Table.glb" -> R.drawable.white_table
        "Red Couch.glb" -> R.drawable.red_couch
        "Brown Couch.glb" -> R.drawable.brown_couch
        "White Couch.glb" -> R.drawable.white_couch
        "Ceiling Fan.glb" -> R.drawable.ceiling_fan
        "Ceiling fan (1).glb" -> R.drawable.ceiling_fan__1_
        "Ceiling Lamp.glb" -> R.drawable.ceiling_lamp
        "Ceiling Light (1).glb" -> R.drawable.ceiling_light__1_
        "Ceiling Light.glb" -> R.drawable.ceiling_light
        "Floor Tile.glb" -> R.drawable.floor_tile
        "Picnic Basket.glb" -> R.drawable.picnic_basket
        "Wall dsk speaker.glb" -> R.drawable.wall_desk_speakers
        "Armchair.glb" -> R.drawable.armchair
        "Bed 3.glb" -> R.drawable.bed_3
        "Bed Double 2.glb" -> R.drawable.bed_double_2
        "Bed Double.glb" -> R.drawable.bed_double
        "Bed King.glb" -> R.drawable.bed_king
        "Bed Single.glb" -> R.drawable.bed_single
        "Bed Twin.glb" -> R.drawable.bed_twin
        "Bed.glb" -> R.drawable.bed
        "Bookcase with Books.glb" -> R.drawable.bookcase_with_books
        "Bookshelf 4.glb" -> R.drawable.bookshelf_4
        "Bookshelf big.glb" -> R.drawable.bookshelf_big
        "Ceiling Lamp 2.glb" -> R.drawable.ceiling_lamp_2
        "Ceiling Light 2.glb" -> R.drawable.ceiling_light_2
        "Ceiling Light Fixture.glb" -> R.drawable.ceiling_light_fixture
        "Ceiling fan 2.glb" -> R.drawable.ceiling_fan_2
        "Chair 1.glb" -> R.drawable.chair_1
        "Chair 2.glb" -> R.drawable.chair_2
        "Chair 4.glb" -> R.drawable.chair_4
        "Chair.glb" -> R.drawable.chair
        "ChairC.glb" -> R.drawable.chairc
        "Chandelier.glb" -> R.drawable.chandelier
        "Closet.glb" -> R.drawable.closet
        "Couch 2.glb" -> R.drawable.couch_2
        "Couch Brown.glb" -> R.drawable.couch_brown
        "Couch Small.glb" -> R.drawable.couch_small
        "Couch Wide.glb" -> R.drawable.couch_wide
        "Couch.glb" -> R.drawable.couch
        "Desk 2.glb" -> R.drawable.desk_2
        "Desk.glb" -> R.drawable.desk
        "Dining Set 2.glb" -> R.drawable.dining_set_2
        "Dining Set.glb" -> R.drawable.dining_set
        "Executive Chair.glb" -> R.drawable.executive_chair
        "Fan.glb" -> R.drawable.fan
        "Flower Pot 2.glb" -> R.drawable.flower_pot_2
        "Flower Pot.glb" -> R.drawable.flower_pot
        "Houseplant.glb" -> R.drawable.houseplant
        "Kitchen Cabinet.glb" -> R.drawable.kitchen_cabinet
        "Kitchen Fridge Large.glb" -> R.drawable.kitchen_fridge_large
        "Kitchen Fridge.glb" -> R.drawable.kitchen_fridge
        "Kitchen Stove.glb" -> R.drawable.kitchen_stove
        "Light Ceiling Single.glb" -> R.drawable.light_ceiling_single
        "Light Ceiling.glb" -> R.drawable.light_ceiling
        "Light Chandelier.glb" -> R.drawable.light_chandelier
        "Lounge Design Sofa Corn.glb" -> R.drawable.lounge_design_sofa_corn
        "Lounge Sofa Corner.glb" -> R.drawable.lounge_sofa_corner
        "Lounge Sofa Ottoman.glb" -> R.drawable.lounge_sofa_ottoman
        "Office Chair 2.glb" -> R.drawable.office_chair_2
        "Office Chair.glb" -> R.drawable.office_chair
        "Oven.glb" -> R.drawable.oven
        "Plants - Assorted shelf plants.glb" -> R.drawable.plants___assorted_shelf_plants
        "Pot Plant.glb" -> R.drawable.pot_plant
        "Room space test.glb" -> R.drawable.room_space_test
        "Shelf Small.glb" -> R.drawable.shelf_small
        "Sofa.glb" -> R.drawable.sofa
        "Sofaa.glb" -> R.drawable.sofaa
        "Table and Chairs.glb" -> R.drawable.table_and_chairs
        "Table.glb" -> R.drawable.table
        "Wall Shelf.glb" -> R.drawable.wall_shelf
        "Wall desk speakers.glb" -> R.drawable.wall_desk_speakers
        "big Couch.glb" -> R.drawable.big_couch
        "shelf 2.glb" -> R.drawable.shelf_2
        "wooden bookshelf 3.glb" -> R.drawable.wooden_bookshelf_3
        "zig zag bookshelf.glb" -> R.drawable.zig_zag_bookshelf
        else -> R.drawable.profile // A default fallback image
    }
}

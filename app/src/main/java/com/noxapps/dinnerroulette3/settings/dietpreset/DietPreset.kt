package com.noxapps.dinnerroulette3.settings.dietpreset

import io.objectbox.Box
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
class DietPreset(
    @Id
    var id:Long = 0,
    var name:String = "",
    var meatContent:Int = 0,//0 = yes meat, 1= no - vegitarian, 2 = no - vegan
    var enabledMeat:MutableList<String> = mutableListOf(),
    var enabledCarb:MutableList<String> = mutableListOf(),
    var availableAppliances:MutableList<String> = mutableListOf(),
    var excludedIngredients:MutableList<String> = mutableListOf(),
    var descriptiveTags: MutableList<String> = mutableListOf()

) {
    override operator fun equals(other: Any?)
            = (other is DietPreset)
            && id == other.id
            && name == other.name
            && meatContent == other.meatContent
            && enabledMeat == other.enabledMeat
            && enabledCarb == other.enabledCarb
            && availableAppliances == other.availableAppliances
            && excludedIngredients == other.excludedIngredients
            && descriptiveTags == other.descriptiveTags

    override fun toString(): String{
        return "id: "+id.toString() +", name: "+name+", meatContent: "+meatContent.toString() +", " +
                "disabled meats: "+enabledMeat.toString() +", disabled carbs: "+enabledCarb.toString() +", " +
                "disables appliances: "+availableAppliances.toString()+ "excluded ingredients: "+
                excludedIngredients.toString() +", tags: "+descriptiveTags.toString()
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + meatContent
        result = 31 * result + enabledMeat.hashCode()
        result = 31 * result + enabledCarb.hashCode()
        result = 31 * result + availableAppliances.hashCode()
        result = 31 * result + excludedIngredients.hashCode()
        result = 31 * result + descriptiveTags.hashCode()
        return result
    }
}

fun initiliseDietPreset(box: Box<DietPreset>){
    box.put(
        DietPreset(
            0,
            "No Preset",
            0,
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf()
        )
    )
    box.put(
        DietPreset(
            0,
            "Coeliacs Disease",
            0,
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf("Gluten"),
            mutableListOf("Gluten Free")
        )
    )
    box.put(
        DietPreset(
            0,
            "Keto Diet",
            0,
            mutableListOf(),
            mutableListOf("None"),
            mutableListOf(),
            mutableListOf("Carbohydrates"),
            mutableListOf("Keto Friendly", "High Fat", "No Carbohydrates")
        )
    )
    box.put(
        DietPreset(
            0,
            "Chron's Preventative Diet",
            0,
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf("Chron's Friendly", "Low in insoluble fibre", "Low fibre", "Low fat",
                "Low lactose", "Low in added sugars", "Not Spicy")
        )
    )
    box.put(
        DietPreset(
            0,
            "Chron's Flare Up Diet",
            0,
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf("Chron's friendly", "High protein", "High in fluids",
                "Low in insoluble fibre", "Low fibre", "Low fat", "Low lactose", "Low in added sugars",
                "Not Spicy")
        )
    )
    box.put(
        DietPreset(
            0,
            "Atkins Diet",
            0,
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf("Atkins Diet Friendly", "High Protein", "High Fat", "Low Carbohydrates"))
    )
    box.put(
        DietPreset(
            0,
            "GallBladder Diet",
            0,
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf("Fried foods","Highly processed foods","Whole milk dairy products"),
            mutableListOf("Gallbladder Friendly", "Low Cholesterol", "Low Fat"))
    )
}
package com.alex.mainmodule.utils

import com.alex.mainmodule.data.Repository
import com.alex.mainmodule.domain.Restaurant
import com.alex.mainmodule.domain.Review
import com.alex.mainmodule.domain.Role
import com.alex.mainmodule.domain.User
import java.util.*
import kotlin.random.Random.Default.nextBoolean

object TestDataSourceObjects {


    fun createDataBaseTest(repository: Repository): ArrayList<Restaurant> {

        getUsersList().forEach {
            repository.addUser(it)
        }

        getRestaurantsList().forEach {
            repository.addRestaurant(it)
        }

        return arrayListOf()
    }

    private fun getRestaurantsList(): ArrayList<Restaurant> {
        var result = arrayListOf<Restaurant>()

        restaurantNames.forEach {
            result.add(
                Restaurant(
                    name = it,
                    ownerEmail = getRandomOwnerEmailAddress(),
                    picture = restaurantPics.random(),
                    address = restaurantAdressList.random(),
                    reviews = getRandomReviews()
                )
            )
        }

        return result
    }

    private fun getRandomReviews(): ArrayList<Review> {
        var result = arrayListOf<Review>()
        var numberOfReviews = (3..7).random()

        for (i in 0..numberOfReviews) {
            result.add(getRandomReview())
        }

        return result
    }

    private fun getRandomReview() =
        Review(
            userEmail = getRandomRegularUserEmailAddress(),
            reply = getRandomReply(),
            description = getRandomLongText(),
            title = getRandomShortText(),
            restaurantOverallEvaluation = (1..5).random().toFloat(),
            visitDateInMillis = getRandomDateMillis(),
            dateInMillis = getRandomDateMillis()
        )


    private fun getRandomDateMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.DAY_OF_MONTH, (2..27).random())
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)

        return calendar.timeInMillis
    }

    private fun getRandomLongText() = loremIpsumLongStrings.random()
    private fun getRandomShortText() = loremIpsumShortStrings.random()

    private fun getRandomReply() =
        if (nextBoolean()) {
            ""
        } else {
            loremIpsumLongStrings.random()
        }


    private fun getRandomOwnerEmailAddress() = getUsersList(Role.OWNER).random().email
    private fun getRandomRegularUserEmailAddress() = getUsersList(Role.REGULAR).random().email


    private fun getUsersList(): ArrayList<User> {
        var result = arrayListOf<User>()

        result.addAll(getUsersList(Role.ADMIN))
        result.addAll(getUsersList(Role.OWNER))
        result.addAll(getUsersList(Role.REGULAR))

        return result
    }

    private fun getUsersList(role: Role): ArrayList<User> {

        val result = arrayListOf<User>()

        ownerUserNames.forEach {
            result.add(
                User(
                    name = it,
                    email = "${it.toLowerCase(Locale.ROOT)}${role.name.toLowerCase(Locale.getDefault())}@$emailDomain",
                    password = "${it.toLowerCase(Locale.ROOT)}${role.name.toLowerCase(Locale.getDefault())}",
                    role = role.name,
                    image = userPics.random()
                )
            )
        }

        return result
    }


    private val ownerUserNames = arrayListOf("Alex", "John", "Maria")
    private val restaurantNames = arrayListOf(
        "KFC",
        "McDonald",
        "Pizza Hut",
        "Burger King",
        "Starbucks",
        "Subway",
        "Taco Bell",
        "Kebap Place",
        "Grill",
        "Classy",
        "Donner",
        "Potato"
    )
    private val restaurantPics = arrayListOf(
        "https://i.imgur.com/Xj59Kw3b.jpg",
        "https://images.all-free-download.com/images/graphiclarge/food_picture_01_hd_pictures_167558.jpg",
        "https://cdn.guidingtech.com/imager/assets/189869/HD-Mouth-Watering-Food-Wallpapers-for-Desktop-12_4d470f76dc99e18ad75087b1b8410ea9.jpg?1573743472",
        "https://cdn.shopify.com/s/files/1/1889/7443/files/blog_-NY-Cleanse-good-mood-600x503_medium.png?v=1529006764",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQMs-7io2h4b4k0a31kDjn71ECWF8dtNJe-PA&usqp=CAU"
    )

    private val userPics = arrayListOf(
        "https://i0.wp.com/post.medicalnewstoday.com/wp-content/uploads/sites/3/2020/03/GettyImages-1092658864_hero-1024x575.jpg?w=1155&h=1528",
        "https://media.glamour.com/photos/5a425fd3b6bcee68da9f86f8/master/pass/best-face-oil.png",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRW9oGqW4-vdo2u-Udu-7ydEfxsRbNABLuU_Q&usqp=CAU",
        "https://i.pinimg.com/736x/3e/dd/46/3edd46376a0328e8e002526d6da7b0ba.jpg",
        "https://i.pinimg.com/736x/5d/d5/35/5dd5353a11972f2d140c2a889cee26da.jpg",
        "https://i.pinimg.com/474x/5c/c4/58/5cc4588b3ecbbeee116f64929e9be76f.jpg",
        "https://i.pinimg.com/originals/5b/ad/df/5baddff9570f0052d667267f2cfcc7eb.jpg"
    )


    private val restaurantAdressList = arrayListOf(
        "8 Stillwater Street Newnan, GA 30263",
        "345 Paris Hill Court Fort Washington, MD 20744",
        "808 Honey Creek St. Venice, FL 34293",
        "8906 East Cypress St. Manassas, VA 20109",
        "322 State St. Suitland, MD 20746",
        "7862 Sherman Street Natchez, MS 39120"
    )

    private val loremIpsumLongStrings = arrayListOf(
        "Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo.",
        "Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus.",
        "Aenean imperdiet. Etiam ultricies nisi vel augue. ",
        "tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum",
        "Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo.",
        "Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus.",
        "ligula eget dolor. Aenean massa.",
        "Lorem ipsum dolor sit amet",
        "Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt.",
    )

    private val loremIpsumShortStrings = arrayListOf(
        "tellus eget condimentum rhoncus",
        "Maecenas nec odio",
        "Integer",
        "Aenean massa.",
        "Lorem ipsum",
        "justo",
        "Maecenas nec odio et ante tincidunt tempus.",
        "Vivamus elementum semper nisi."
    )

    private const val emailDomain = "toptal.com"


}
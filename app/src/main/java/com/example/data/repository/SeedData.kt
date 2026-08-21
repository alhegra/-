package com.example.data.repository

import com.example.data.model.*

object SeedData {

    val sampleAddresses = listOf(
        Address(
            id = "addr_1",
            label = "البيت (مدينة نصر)",
            governorate = "القاهرة",
            city = "مدينة نصر",
            area = "منطقة عباس العقاد",
            street = "شارع سمير عبد الرؤوف",
            buildingNumber = "عمارة 24",
            floor = "الدور الرابع",
            apartment = "شقة 12",
            landmark = "بجوار صيدلية العزبي وسوبرماركت سعودي",
            deliveryInstructions = "رن الجرس واترك الطلب أمام الباب",
            latitude = 30.0561,
            longitude = 31.3412
        ),
        Address(
            id = "addr_2",
            label = "الشغل (التجمع الخامس)",
            governorate = "القاهرة الجديدة",
            city = "التجمع الخامس",
            area = "شارع التسعين الشمالي",
            street = "مجمع البنوك والأعمال",
            buildingNumber = "مبنى B2",
            floor = "الدور الثاني",
            apartment = "مكتب 204",
            landmark = "أمام مول كايرو فيستيفال سيتي",
            deliveryInstructions = "الاتصال عند الوصول للاستلام من الاستقبال",
            latitude = 30.0245,
            longitude = 31.4398
        ),
        Address(
            id = "addr_3",
            label = "بيت العيلة (الدقي)",
            governorate = "الجيزة",
            city = "الدقي",
            area = "شارع مصدق",
            street = "شارع محيي الدين أبو العز",
            buildingNumber = "عمارة 15",
            floor = "الدور السادس",
            apartment = "شقة 601",
            landmark = "خلف محطة مترو البحوث",
            deliveryInstructions = "المصعد يعمل برقم سري، يرجى الرن من الإنتركم",
            latitude = 30.0384,
            longitude = 31.2124
        )
    )

    val categories = listOf(
        "الكل" to "🍽️",
        "برجر" to "🍔",
        "كشري ومصري" to "🍲",
        "مشويات وكباب" to "🍗",
        "شاورما وساندوتشات" to "🌯",
        "بيتزا وفطير" to "🍕",
        "كريب ووافل" to "🥞",
        "حلويات وآيس كريم" to "🍨",
        "كافيهات وعصائر" to "☕"
    )

    val restaurants = listOf(
        Restaurant(
            id = "rest_1",
            name = "كشري أبو طارق",
            nameEn = "Koshary Abou Tarek",
            logoUrl = "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=300",
            coverUrl = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=900",
            cuisines = listOf("كشري ومصري", "أكل شرقي"),
            rating = 4.8,
            reviewCount = 1420,
            deliveryTimeMinutes = 25,
            deliveryFee = 15.0,
            minOrder = 40.0,
            area = "وسط البلد / مدينة نصر",
            discountBadge = "خصم 20% بكود MINYOO",
            isOpen = true,
            isFeatured = true
        ),
        Restaurant(
            id = "rest_2",
            name = "بافلو برجر - Buffalo Burger",
            nameEn = "Buffalo Burger",
            logoUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=300",
            coverUrl = "https://images.unsplash.com/photo-1550547660-d9450f859349?w=900",
            cuisines = listOf("برجر", "ساندوتشات"),
            rating = 4.7,
            reviewCount = 980,
            deliveryTimeMinutes = 35,
            deliveryFee = 20.0,
            minOrder = 80.0,
            area = "مدينة نصر / مصر الجديدة",
            discountBadge = "توصيل مجاني للطلبات فوق 150ج",
            isOpen = true,
            isFeatured = true
        ),
        Restaurant(
            id = "rest_3",
            name = "قصر الكبابجي",
            nameEn = "Kasr El Kababgy",
            logoUrl = "https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=300",
            coverUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?w=900",
            cuisines = listOf("مشويات وكباب", "طواجن"),
            rating = 4.9,
            reviewCount = 2150,
            deliveryTimeMinutes = 45,
            deliveryFee = 25.0,
            minOrder = 150.0,
            area = "التجمع الخامس / الشيخ زايد",
            discountBadge = "سفرة الملوك مع صواني مشكلة",
            isOpen = true,
            isFeatured = true
        ),
        Restaurant(
            id = "rest_4",
            name = "بلبن - B.Laban",
            nameEn = "B.Laban",
            logoUrl = "https://images.unsplash.com/photo-1551024709-8f23befc6f87?w=300",
            coverUrl = "https://images.unsplash.com/photo-1576618148400-f54bed99fcfd?w=900",
            cuisines = listOf("حلويات وآيس كريم", "قشطوطة وأم علي"),
            rating = 4.9,
            reviewCount = 3400,
            deliveryTimeMinutes = 20,
            deliveryFee = 15.0,
            minOrder = 35.0,
            area = "كل فروع القاهرة والجيزة",
            discountBadge = "الأكثر طلباً وترند مصر 🔥",
            isOpen = true,
            isFeatured = true
        ),
        Restaurant(
            id = "rest_5",
            name = "شاورما الريم السورية",
            nameEn = "Shawarma El Reem",
            logoUrl = "https://images.unsplash.com/photo-1529006557810-274b9b2fc783?w=300",
            coverUrl = "https://images.unsplash.com/photo-1561651823-34feb02250e4?w=900",
            cuisines = listOf("شاورما وساندوتشات", "أكل سوري"),
            rating = 4.6,
            reviewCount = 870,
            deliveryTimeMinutes = 30,
            deliveryFee = 18.0,
            minOrder = 60.0,
            area = "الدقي / المهندسين",
            discountBadge = "ساندوتش دبل شاورما هدية",
            isOpen = true,
            isFeatured = false
        ),
        Restaurant(
            id = "rest_6",
            name = "بريموز بيتزا - Primo's Pizza",
            nameEn = "Primo's Pizza",
            logoUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=300",
            coverUrl = "https://images.unsplash.com/photo-1534308983496-4fabb1a015ee?w=900",
            cuisines = listOf("بيتزا وفطير", "باستا"),
            rating = 4.5,
            reviewCount = 650,
            deliveryTimeMinutes = 35,
            deliveryFee = 20.0,
            minOrder = 90.0,
            area = "المعادي / الزمالك",
            discountBadge = "اشتري 1 واحصل على 1 بنصف السعر",
            isOpen = true,
            isFeatured = false
        ),
        Restaurant(
            id = "rest_7",
            name = "سيتي كريب - City Crepe",
            nameEn = "City Crepe",
            logoUrl = "https://images.unsplash.com/photo-1519676867240-f03562e64548?w=300",
            coverUrl = "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=900",
            cuisines = listOf("كريب ووافل", "ساندوتشات"),
            rating = 4.4,
            reviewCount = 520,
            deliveryTimeMinutes = 25,
            deliveryFee = 15.0,
            minOrder = 50.0,
            area = "عين شمس / مصر الجديدة",
            discountBadge = "عروض كومبو الغلابة والشباب",
            isOpen = true,
            isFeatured = false
        ),
        Restaurant(
            id = "rest_8",
            name = "حواوشي الرفاعي الأصلي",
            nameEn = "Hawawshi El Refaey",
            logoUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?w=300",
            coverUrl = "https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=900",
            cuisines = listOf("كشري ومصري", "مشويات وكباب"),
            rating = 4.8,
            reviewCount = 1100,
            deliveryTimeMinutes = 30,
            deliveryFee = 15.0,
            minOrder = 45.0,
            area = "عابدين / وسط البلد",
            discountBadge = "حواوشي بلدي سمنة فلاحي",
            isOpen = true,
            isFeatured = false
        ),
        Restaurant(
            id = "rest_9",
            name = "كافيه بينوس - Beanos Cafe",
            nameEn = "Beanos Cafe",
            logoUrl = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=300",
            coverUrl = "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=900",
            cuisines = listOf("كافيهات وعصائر", "حلويات وآيس كريم"),
            rating = 4.6,
            reviewCount = 410,
            deliveryTimeMinutes = 20,
            deliveryFee = 20.0,
            minOrder = 70.0,
            area = "الزمالك / كوران الكورنيش",
            discountBadge = "قهوة متخصصة وكرواسون طازج",
            isOpen = true,
            isFeatured = false
        ),
        Restaurant(
            id = "rest_10",
            name = "زوزو - أكلات مصرية شرقية",
            nameEn = "Zooba Egyptian Street Food",
            logoUrl = "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=300",
            coverUrl = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=900",
            cuisines = listOf("كشري ومصري", "طواجن"),
            rating = 4.7,
            reviewCount = 930,
            deliveryTimeMinutes = 30,
            deliveryFee = 18.0,
            minOrder = 60.0,
            area = "الزمالك / التجمع",
            discountBadge = "طواجن وفول وفلافل بريميوم",
            isOpen = true,
            isFeatured = true
        )
    )

    val products = listOf(
        // Koshary Abou Tarek (rest_1)
        Product(
            id = "p_101",
            restaurantId = "rest_1",
            categoryId = "الأطباق الرئيسية",
            name = "علبة كشري سوبر لوكس أبو طارق",
            description = "أرز بالشعرية، مكرونة مشكلة، عدس بجبة، حمص الشام، بصل مقرمش ذهبي مع الصلصة المسبوكة والدقة والشطة الزيتية الحامية.",
            price = 45.0,
            originalPrice = 55.0,
            imageUrl = "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=600",
            isPopular = true,
            modifierGroups = listOf(
                ModifierGroup(
                    id = "mg_size_101",
                    title = "اختر الحجم",
                    isRequired = true,
                    minSelection = 1,
                    maxSelection = 1,
                    options = listOf(
                        ModifierOption("opt_1", "حجم وسط", 0.0),
                        ModifierOption("opt_2", "حجم كبير لارج", 15.0),
                        ModifierOption("opt_3", "حجم عائلي جامبو", 35.0)
                    )
                ),
                ModifierGroup(
                    id = "mg_extra_101",
                    title = "الإضافات اللذيذة",
                    isRequired = false,
                    minSelection = 0,
                    maxSelection = 3,
                    options = listOf(
                        ModifierOption("opt_ex1", "تقلية بصل مقرمش زيادة", 10.0),
                        ModifierOption("opt_ex2", "صلصة طماطم مسبوكة زيادة", 8.0),
                        ModifierOption("opt_ex3", "حمص شام وعدس إضافي", 10.0),
                        ModifierOption("opt_ex4", "شطة زيت لهاليبو", 5.0)
                    )
                ),
                ModifierGroup(
                    id = "mg_drink_101",
                    title = "المشروب والتحلية",
                    isRequired = false,
                    minSelection = 0,
                    maxSelection = 1,
                    options = listOf(
                        ModifierOption("opt_dr1", "أرز باللبن والمكسرات أبو طارق", 25.0),
                        ModifierOption("opt_dr2", "كانز بيبسي ساقع مشبر", 18.0),
                        ModifierOption("opt_dr3", "مياه معدنية مثلجة", 8.0)
                    )
                )
            )
        ),
        Product(
            id = "p_102",
            restaurantId = "rest_1",
            categoryId = "الأطباق الرئيسية",
            name = "طاجن مكرونة باللحمة المفرومة البلدي",
            description = "مكرونة فرن باللحمة المفرومة البلدي المتبلة بالبهارات المصرية والصلصة الحمراء الغنية في الفرن الحجري.",
            price = 60.0,
            originalPrice = 70.0,
            imageUrl = "https://images.unsplash.com/photo-1551183053-bf91a1d81141?w=600",
            isPopular = true,
            modifierGroups = listOf(
                ModifierGroup(
                    id = "mg_extra_102",
                    title = "إضافات الجبن والصلصة",
                    isRequired = false,
                    minSelection = 0,
                    maxSelection = 2,
                    options = listOf(
                        ModifierOption("opt_t1", "جبنة موتزاريلا سايحة على الوش", 20.0),
                        ModifierOption("opt_t2", "دقة حارة إضافية", 6.0)
                    )
                )
            )
        ),
        Product(
            id = "p_103",
            restaurantId = "rest_1",
            categoryId = "الحلويات",
            name = "أرز باللبن فرن بالقشطة والمكسرات",
            description = "أرز باللبن طبيعي 100% محمر بالفرن مع طبقة قشطة فلاحي ومكسرات بندق ولوز وزبيب.",
            price = 30.0,
            imageUrl = "https://images.unsplash.com/photo-1576618148400-f54bed99fcfd?w=600",
            isPopular = false
        ),

        // Buffalo Burger (rest_2)
        Product(
            id = "p_201",
            restaurantId = "rest_2",
            categoryId = "ساندوتشات البرجر",
            name = "برجر شيروكي تشيزي بيكون - Cherokee Bacon",
            description = "قطعة لحم بقري صافي مشوية على اللهب 200 جرام، جبنة شيدر مدخنة، بيكون بقري مقرمش، صوص البافلو السري، بصل مكرمل وخس فريش.",
            price = 145.0,
            originalPrice = 170.0,
            imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600",
            isPopular = true,
            modifierGroups = listOf(
                ModifierGroup(
                    id = "mg_size_201",
                    title = "حجم البرجر وعدد القطع",
                    isRequired = true,
                    minSelection = 1,
                    maxSelection = 1,
                    options = listOf(
                        ModifierOption("opt_b1", "سينجل 200 جرام", 0.0),
                        ModifierOption("opt_b2", "دبل 400 جرام سوبر مشبع", 60.0),
                        ModifierOption("opt_b3", "تريبل 600 جرام وحش الجوع", 110.0)
                    )
                ),
                ModifierGroup(
                    id = "mg_combo_201",
                    title = "اجعلها وجبة كومبو",
                    isRequired = false,
                    minSelection = 0,
                    maxSelection = 1,
                    options = listOf(
                        ModifierOption("opt_cb1", "كومبو بطاطس ودجز + بيبسي", 45.0),
                        ModifierOption("opt_cb2", "كومبو بطاطس بالجبنة الشيدر والهلابينو + مشروب", 65.0),
                        ModifierOption("opt_cb3", "كومبو حلقات بصل كريسبي + صوص باربيكيو + مشروب", 55.0)
                    )
                ),
                ModifierGroup(
                    id = "mg_addons_201",
                    title = "إضافات مخصصة",
                    isRequired = false,
                    minSelection = 0,
                    maxSelection = 3,
                    options = listOf(
                        ModifierOption("opt_ad1", "إكسترا جبنة شيدر سايحة", 20.0),
                        ModifierOption("opt_ad2", "مخلل هالبينو حار", 12.0),
                        ModifierOption("opt_ad3", "مشروم سوتيه بالزبدة", 25.0)
                    )
                )
            )
        ),
        Product(
            id = "p_202",
            restaurantId = "rest_2",
            categoryId = "ساندوتشات الفراخ",
            name = "تشيكن كريسبي باسترما هالبينو",
            description = "صدر دجاج مقرمش ذهبي حار، طبقة بسطرمة مشوحة، جبنة أمريكي، صوص رانش وهالبينو في خبز بريوش طازج.",
            price = 130.0,
            imageUrl = "https://images.unsplash.com/photo-1625813506062-0aeb1d7a094b?w=600",
            isPopular = true
        ),
        Product(
            id = "p_203",
            restaurantId = "rest_2",
            categoryId = "المقبلات والبطاطس",
            name = "تشيزي لودد فرايز بالبيكون والهلابينو",
            description = "طبق بطاطس مقلية ذهبية مغطاة بصوص الجبنة الشيدر الغني وقطع البيكون والهلابينو الحار.",
            price = 65.0,
            imageUrl = "https://images.unsplash.com/photo-1586190848861-99aa4a171e90?w=600"
        ),

        // Kasr El Kababgy (rest_3)
        Product(
            id = "p_301",
            restaurantId = "rest_3",
            categoryId = "المشويات الملكية",
            name = "صينية مشكل كباب وكفتة وريش ضاني",
            description = "نصف كيلو مشويات مشكلة (كفتة بلدي متبلة، كباب بتلو ناعم، ريش ضاني مشوية على الفحم) مع أرز بسمتي بالخلطة، طحينة وسلطة خضراء وعيش سخن.",
            price = 390.0,
            originalPrice = 450.0,
            imageUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?w=600",
            isPopular = true,
            modifierGroups = listOf(
                ModifierGroup(
                    id = "mg_weight_301",
                    title = "وزن الصينية",
                    isRequired = true,
                    minSelection = 1,
                    maxSelection = 1,
                    options = listOf(
                        ModifierOption("opt_w1", "نصف كيلو (شخصين)", 0.0),
                        ModifierOption("opt_w2", "كيلو كامل (3-4 أفراد)", 350.0),
                        ModifierOption("opt_w3", "صينية الملوك 2 كيلو عائلية", 980.0)
                    )
                ),
                ModifierGroup(
                    id = "mg_salads_301",
                    title = "السلطات والمقبلات الإضافية",
                    isRequired = false,
                    minSelection = 0,
                    maxSelection = 3,
                    options = listOf(
                        ModifierOption("opt_sl1", "طحينة بيضاء بلدي زيادة", 15.0),
                        ModifierOption("opt_sl2", "بابا غنوج مشوي عالفحم", 18.0),
                        ModifierOption("opt_sl3", "سلطة بلدي دقة وليمون", 12.0),
                        ModifierOption("opt_sl4", "ممبار بلدي محمر (4 قطع)", 45.0)
                    )
                )
            )
        ),
        Product(
            id = "p_302",
            restaurantId = "rest_3",
            categoryId = "الطواجن والشوربات",
            name = "طاجن عكاوي بالبصل القاورما في الفرن",
            description = "عكاوي بلدي مستوية على نار هادية بالسمنة البلدي والبصل المكرمل داخل طاجن فخار مصري أصيل.",
            price = 260.0,
            imageUrl = "https://images.unsplash.com/photo-1541832676-9b763b0239ab?w=600",
            isPopular = true
        ),
        Product(
            id = "p_303",
            restaurantId = "rest_3",
            categoryId = "المشويات الملكية",
            name = "فرخة مشوية عالفحم مع أرز بسمتي وطحينة",
            description = "دجاجة كاملة متبلة بخلطة قصر الكبابجي السرية مشوية عالفحم مع بطاطس وسلطات وعيش.",
            price = 210.0,
            imageUrl = "https://images.unsplash.com/photo-1598103442097-8b74394b95c6?w=600"
        ),

        // B.Laban (rest_4)
        Product(
            id = "p_401",
            restaurantId = "rest_4",
            categoryId = "القشطوطة والتريند",
            name = "قشطوطة لوتس ونوتيلا غرقانة قشطة",
            description = "كيكة الحليب التركية المشبعة بالقشطة الفلاحي ومغطاة بطبقات وفيرة من زبدة اللوتس وشوكولاتة النوتيلا ومكسرات البندق.",
            price = 65.0,
            originalPrice = 75.0,
            imageUrl = "https://images.unsplash.com/photo-1551024709-8f23befc6f87?w=600",
            isPopular = true,
            modifierGroups = listOf(
                ModifierGroup(
                    id = "mg_ice_401",
                    title = "إضافة آيس كريم وبولا جيلاتو",
                    isRequired = false,
                    minSelection = 0,
                    maxSelection = 1,
                    options = listOf(
                        ModifierOption("opt_ic1", "بولا جيلاتو مستكة بلدي", 20.0),
                        ModifierOption("opt_ic2", "بولا جيلاتو فانيليا أوريو", 18.0),
                        ModifierOption("opt_ic3", "بولا مانجو فريش طبيعي", 20.0)
                    )
                ),
                ModifierGroup(
                    id = "mg_nuts_401",
                    title = "مكسرات وفستق",
                    isRequired = false,
                    minSelection = 0,
                    maxSelection = 2,
                    options = listOf(
                        ModifierOption("opt_nt1", "فستق حلبي مدقوق زيادة", 25.0),
                        ModifierOption("opt_nt2", "صلصة كيندر إكسترا", 18.0)
                    )
                )
            )
        ),
        Product(
            id = "p_402",
            restaurantId = "rest_4",
            categoryId = "القشطوطة والتريند",
            name = "كشري بلبن الحلو - مانجو وبيستاشيو",
            description = "طبقات من الجلاش المقرمش والكنافة المحمرة والكريمة اللباني وقطع المانجو الفريش وزبدة الفستق الإيطالية.",
            price = 70.0,
            imageUrl = "https://images.unsplash.com/photo-1563729784474-d77dbb933a9e?w=600",
            isPopular = true
        ),
        Product(
            id = "p_403",
            restaurantId = "rest_4",
            categoryId = "أم علي والحلويات الشرقية",
            name = "طاجن أم علي ملوكي بالسمنة والمكسرات",
            description = "رقاق بلبن طبيعي مغلي ومكسرات وزبدة فلاحي محمرة في الفرن.",
            price = 45.0,
            imageUrl = "https://images.unsplash.com/photo-1587314168485-3236d6710814?w=600"
        ),

        // Shawarma El Reem (rest_5)
        Product(
            id = "p_501",
            restaurantId = "rest_5",
            categoryId = "الشاورما السورية",
            name = "وجبة عربي شاورما لحمة سوري دبل",
            description = "ساندوتش شاورما لحمة متبلة مقطعة رولات مع صوص الطحينة البيوز وبطاطس محمرة ومخلل خيار ولفت وثومية حارة.",
            price = 115.0,
            originalPrice = 130.0,
            imageUrl = "https://images.unsplash.com/photo-1529006557810-274b9b2fc783?w=600",
            isPopular = true,
            modifierGroups = listOf(
                ModifierGroup(
                    id = "mg_size_501",
                    title = "حجم الوجبة",
                    isRequired = true,
                    minSelection = 1,
                    maxSelection = 1,
                    options = listOf(
                        ModifierOption("opt_sh1", "وجبة عربي مفردة (ساندوتش دبل)", 0.0),
                        ModifierOption("opt_sh2", "وجبة عربي تريبل (شخصين)", 70.0)
                    )
                ),
                ModifierGroup(
                    id = "mg_sauce_501",
                    title = "الثومية والمقبلات",
                    isRequired = false,
                    minSelection = 0,
                    maxSelection = 2,
                    options = listOf(
                        ModifierOption("opt_sc1", "ثومية حارة سبايسي", 10.0),
                        ModifierOption("opt_sc2", "دبس رمان إضافي على اللحمة", 12.0)
                    )
                )
            )
        ),
        Product(
            id = "p_502",
            restaurantId = "rest_5",
            categoryId = "الشاورما السورية",
            name = "ساندوتش شاورما فراخ صاروخ صاج بالثومية والمخلل",
            description = "شاورما دجاج متبلة في خبز صاج سوري محمص على الجريل مع الثومية الأصلية.",
            price = 65.0,
            imageUrl = "https://images.unsplash.com/photo-1561651823-34feb02250e4?w=600",
            isPopular = true
        ),

        // Primo's Pizza (rest_6)
        Product(
            id = "p_601",
            restaurantId = "rest_6",
            categoryId = "البيتزا الإيطالية والشرقية",
            name = "بيتزا رانش تشيكن كرانشي لارج",
            description = "عجينة إيطالية مخمرة 48 ساعة، صوص رانش غني، قطع دجاج كرسبي، فلفل ألوان، هالبينو، جبنة موتزاريلا طبيعية مطاطية.",
            price = 165.0,
            originalPrice = 195.0,
            imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=600",
            isPopular = true,
            modifierGroups = listOf(
                ModifierGroup(
                    id = "mg_crust_601",
                    title = "نوع وحافة العجينة",
                    isRequired = true,
                    minSelection = 1,
                    maxSelection = 1,
                    options = listOf(
                        ModifierOption("opt_cr1", "عجينة إيطالية كلاسيك رفيعة", 0.0),
                        ModifierOption("opt_cr2", "أطراف محشوة جبنة شيدر وهوت دوج (Stuffed Crust)", 35.0),
                        ModifierOption("opt_cr3", "عجينة سميكة بان (Pan Crust)", 15.0)
                    )
                )
            )
        ),
        Product(
            id = "p_602",
            restaurantId = "rest_6",
            categoryId = "البيتزا الإيطالية والشرقية",
            name = "بيتزا سوبر سوبريم ميت لافر",
            description = "سجق بلدي، ببروني إيطالي، بيكون بقري، مشروم طازج، زيتون كالاماتا وجبنة موتزاريلا.",
            price = 175.0,
            imageUrl = "https://images.unsplash.com/photo-1534308983496-4fabb1a015ee?w=600"
        ),

        // City Crepe (rest_7)
        Product(
            id = "p_701",
            restaurantId = "rest_7",
            categoryId = "الكريب الحادق",
            name = "كريب سوبر كرانشي زنجر ميكس جبن",
            description = "كريب مقرمش محشو أصابع استربس فراخ حارة، بطاطس، زيتون، رومي، شيدر وموتزاريلا مع مايونيز وكاتشب حار.",
            price = 85.0,
            imageUrl = "https://images.unsplash.com/photo-1519676867240-f03562e64548?w=600",
            isPopular = true
        ),
        Product(
            id = "p_702",
            restaurantId = "rest_7",
            categoryId = "الكريب الحلو",
            name = "كريب نوتيلا ميكس فواكه ومكسرات",
            description = "كريب شوكولاتة نوتيلا أصلية مع موز وفراولة وبندق مقرمش وصوص شوكولاتة بيضاء.",
            price = 60.0,
            imageUrl = "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=600"
        ),

        // Hawawshi El Refaey (rest_8)
        Product(
            id = "p_801",
            restaurantId = "rest_8",
            categoryId = "الحواوشي البلدي",
            name = "حواوشي لحمة بلدي بالسمنة الفلاحي والجبنة الموتزاريلا",
            description = "عيش بلدي طازج محشو لحمة مفرومة بلدي متبلة بالبهارات المصرية واللية الضاني مع ميكس جبن سايحة ومقرمش من برة وطري من جوة.",
            price = 55.0,
            originalPrice = 65.0,
            imageUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?w=600",
            isPopular = true
        ),
        Product(
            id = "p_802",
            restaurantId = "rest_8",
            categoryId = "الحواوشي البلدي",
            name = "حواوشي سجق إسكندراني حار وموتزاريلا",
            description = "سجق بلدي متبل مع طماطم وفلفل حار وجبنة سايحة في رغيف بلدي مقرمش.",
            price = 60.0,
            imageUrl = "https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=600"
        ),

        // Beanos Cafe (rest_9)
        Product(
            id = "p_901",
            restaurantId = "rest_9",
            categoryId = "المشروبات والقهوة",
            name = "سبانش لاتيه مثلج - Iced Spanish Latte",
            description = "إسبريسو دبل شوت مع حليب طازج وحليب مكثف محلى وثلج منعش.",
            price = 55.0,
            imageUrl = "https://images.unsplash.com/photo-1517256064527-09c73fc73e38?w=600",
            isPopular = true
        ),
        Product(
            id = "p_902",
            restaurantId = "rest_9",
            categoryId = "المخبوزات",
            name = "كرواسون زبدة محشو جبنة شيدر ورومي مدخن",
            description = "كرواسون فرنسي هش مورق مخبوز يومياً.",
            price = 45.0,
            imageUrl = "https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=600"
        ),

        // Zooba (rest_10)
        Product(
            id = "p_1001",
            restaurantId = "rest_10",
            categoryId = "أكلات مصرية مبتكرة",
            name = "فول بالسمنة البلدي والبسطرمة والبيض العيون",
            description = "فول مدمس بطريقة زوزو بالسمن البلدي الفلاحي وقطع بسطرمة محمرة وبيض مقلي.",
            price = 48.0,
            imageUrl = "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=600",
            isPopular = true
        ),
        Product(
            id = "p_1002",
            restaurantId = "rest_10",
            categoryId = "أكلات مصرية مبتكرة",
            name = "ساندوتش فلافل محشوة جبنة حلوم وسماق",
            description = "طعمية مصرية مقرمشة محشوة جبنة حلوم في عيش بلدي مع طحينة بنجر وسلطة بلدي.",
            price = 35.0,
            imageUrl = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=600"
        )
    )

    val sampleCoupons = listOf(
        Coupon(
            code = "MINYOO50",
            discountPercentage = 20.0,
            maxDiscount = 50.0,
            minOrder = 80.0,
            description = "خصم 20% بحد أقصى 50 جنيه على أي طلب لأول مستخدمين!",
            isFirstOrderOnly = false
        ),
        Coupon(
            code = "WELD_BALAD",
            fixedDiscount = 30.0,
            maxDiscount = 30.0,
            minOrder = 100.0,
            description = "خصم 30 جنيه مصري مباشر لأولاد البلد 🇪🇬",
            isFirstOrderOnly = false
        ),
        Coupon(
            code = "FREEDEL",
            fixedDiscount = 20.0,
            maxDiscount = 20.0,
            minOrder = 120.0,
            description = "توصيل مجاني للطلبات فوق 120 جنيه",
            isFirstOrderOnly = false
        )
    )

    val sampleNotifications = listOf(
        NotificationItem(
            id = "notif_1",
            title = "كود خصم جديد نزل في حسابك! 🎁",
            body = "استخدم كود WELD_BALAD واحصل على خصم 30 جنيه على وجبتك المفضلة اليوم.",
            timeAgo = "منذ 15 دقيقة",
            isRead = false
        ),
        NotificationItem(
            id = "notif_2",
            title = "عروض الغدا ولعت مع MINYOO 🍔",
            body = "خصومات تصل إلى 40% على جميع مطاعم البرجر والشاورما في منطقتك.",
            timeAgo = "منذ ساعة",
            isRead = false
        ),
        NotificationItem(
            id = "notif_3",
            title = "طلبك السابق تم تسليمه بنجاح ✅",
            body = "شكراً لطلبك من كشري أبو طارق. شاركنا رأيك في تجربة التوصيل وجودة الطعام.",
            timeAgo = "أمس",
            isRead = true
        )
    )
}

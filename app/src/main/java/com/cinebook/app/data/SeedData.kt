package com.cinebook.app.data

object SeedData {
    val showtimes = listOf("10:30", "13:15", "16:00", "19:00", "21:45")

    // ราคาตามแถว: A-B ด้านหน้า, C-D กลาง, E-F ด้านหลัง
    fun seatPrice(row: Char): Int = when (row) {
        'A', 'B' -> 200
        'C', 'D' -> 240
        else -> 280
    }

    val movies = listOf(
        Movie(
            title = "Galaxy Runners",
            genre = "ไซไฟ",
            durationMin = 128,
            rating = 8.1,
            synopsis = "ทีมนักบินอวกาศหนุ่มสาวต้องแข่งกับเวลาเพื่อพาประชากรกลุ่มสุดท้ายหนีออกจากระบบสุริยะที่กำลังล่มสลาย",
            colorStart = 0xFF2B5876, colorEnd = 0xFF4E4376, emoji = "🚀",
            isNowShowing = true, releaseDate = "กำลังฉาย"
        ),
        Movie(
            title = "ปราสาทเงา",
            genre = "สยองขวัญ",
            durationMin = 105,
            rating = 7.4,
            synopsis = "กลุ่มเพื่อนเข้าพักในปราสาทเก่ากลางป่า แล้วพบว่าเงาของพวกเขาเริ่มเคลื่อนไหวด้วยตัวเอง",
            colorStart = 0xFF232526, colorEnd = 0xFF5B2A86, emoji = "👻",
            isNowShowing = true, releaseDate = "กำลังฉาย"
        ),
        Movie(
            title = "รักนี้ที่ปลายฟ้า",
            genre = "โรแมนติก",
            durationMin = 112,
            rating = 8.0,
            synopsis = "นักบินสาวกับช่างภาพหนุ่มพบกันบนเที่ยวบินสุดท้ายของปี และความรักของทั้งคู่ก็เริ่มต้นเหนือก้อนเมฆ",
            colorStart = 0xFFFF758C, colorEnd = 0xFFFF7EB3, emoji = "💕",
            isNowShowing = true, releaseDate = "กำลังฉาย"
        ),
        Movie(
            title = "Turbo Chef",
            genre = "คอมเมดี้",
            durationMin = 98,
            rating = 7.2,
            synopsis = "เชฟขี้ลืมต้องทำอาหารให้ผู้ตรวจมิชลินภายใน 30 นาที ท่ามกลางความวุ่นวายสุดป่วนในครัว",
            colorStart = 0xFFF7971E, colorEnd = 0xFFFFD200, emoji = "🍳",
            isNowShowing = true, releaseDate = "กำลังฉาย"
        ),
        Movie(
            title = "Dragon Peak",
            genre = "แฟนตาซี",
            durationMin = 140,
            rating = 8.5,
            synopsis = "เด็กหนุ่มชาวบ้านได้รับไข่มังกรปริศนา และต้องเดินทางขึ้นยอดเขาต้องห้ามเพื่อปกป้องอาณาจักร",
            colorStart = 0xFF11998E, colorEnd = 0xFF38EF7D, emoji = "🐉",
            isNowShowing = false, releaseDate = "24 ต.ค. 2026"
        ),
        Movie(
            title = "Last Detective",
            genre = "ทริลเลอร์",
            durationMin = 119,
            rating = 7.8,
            synopsis = "นักสืบใกล้เกษียณรับคดีสุดท้าย ซึ่งโยงกลับไปยังคดีเก่าที่เขาไม่เคยคลี่คลายเมื่อ 20 ปีก่อน",
            colorStart = 0xFF414345, colorEnd = 0xFF1F4037, emoji = "🕵️",
            isNowShowing = false, releaseDate = "31 ต.ค. 2026"
        ),
        Movie(
            title = "Cyberpunk Odyssey",
            genre = "ไซไฟ",
            durationMin = 135,
            rating = 0.0,
            synopsis = "การผจญภัยในโลกอนาคตที่เทคโนโลยีควบคุมทุกสรรพสิ่ง และมีเพียงแฮกเกอร์สาวคนเดียวที่กู้ระบบได้",
            colorStart = 0xFF8A2387, colorEnd = 0xFFE94057, emoji = "🌆",
            isNowShowing = false, releaseDate = "7 พ.ย. 2026"
        )
    )
}

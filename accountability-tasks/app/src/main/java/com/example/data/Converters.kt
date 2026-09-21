package com.example.data

import androidx.room.TypeConverter

class Converters {
  @TypeConverter
  fun fromPriority(priority: Priority): String = priority.name

  @TypeConverter
  fun toPriority(value: String): Priority = try {
    Priority.valueOf(value)
  } catch (e: Exception) {
    Priority.MEDIUM
  }

  @TypeConverter
  fun fromPunishmentType(type: PunishmentType): String = type.name

  @TypeConverter
  fun toPunishmentType(value: String): PunishmentType = try {
    PunishmentType.valueOf(value)
  } catch (e: Exception) {
    PunishmentType.DOUBLE_WORK
  }
}

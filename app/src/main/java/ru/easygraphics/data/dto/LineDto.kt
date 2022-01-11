package ru.easygraphics.data.dto

/** Вспомогательная сущность линий для экспорта и импорта графиков */
data class LineDto(
    val title: String,
    val color: String,
    val yValues: List<Double>
)

/*return new GsonBuilder().create().fromJson(new String(decryptData), clazz);

GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			DataStandardSwitches dataStandardSwitches = gson.fromJson(standardSwitch, DataStandardSwitches.class);
			AppLogger.debug("dataStandardSwitches = " + dataStandardSwitches.toString());*/

/*DataStandardSwitches dataStandardSwitches = new DataStandardSwitches(true);
Gson gson = new Gson();
AppLogger.debug("dataStandardSwitches = " + gson.toJson(dataStandardSwitches));
*/
### Экспорти импорт данных
Экспорт и импорт делается из json-объекта. Основная структура описана в [json_scheme_data.json](#json_scheme_data.json), параметры графика и подписи по оси X - в [json_scheme_chart.json](#json_scheme_chart.json), а параметры линий и подписей по оси Y - в [json_scheme_line.json](json_scheme_line.json).

___Пример данных___
```json
{
  "version": 1,
  "date": "17.12.2021",
  "charts": [
    {
      "title": "График температур",
      "countDecimal": 0,
      "xValueType": 3,
      "xValueDateFormat": 2,
      "xName": "Дата",
      "yName": "Температура",
      "xValues": ["10.10", "11.10", "12.10", "13.10", "14.10"],
      "lines": [
        {
          "title": "Утром",
          "color": "#00D7FF",
          "yValues": [10, 12, 15, 20, 18]
        },
        {
          "title": "Днем",
          "color": "#30BF56",
          "yValues": [14, 14, 18, 19, 22]
        },
        {
          "title": "Вечером",
          "color": "#5C5269",
          "yValues": [7, 9, 9, 12, 15]
        }
      ]
    }
  ]
}
```

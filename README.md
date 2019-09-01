"# javavacparser" 

***Парсер вакансий на сайте SQL.ru

Программа ищет вакансии на сайте SQL.ru, связанные с языком Java. Найденные вакансии записываются в базу данных в таблицу вида vacancies(id, name, text, link, create_date).  
Если программа запущена впервые, то она найдет все вакансии за 2019 год. 
Если программ запущена повторно, то будет искать вакансии вплоть до последней даты вакансии из БД.
Для парсинга html используется библиотека Jsoup. 
Программа использует планировщик заданий quartz scheduler для самостоятельного выполнения согласно заданному расписанию.
Периодичность запуска приложения, а так же настройки для подключения к БД указаны в файле app.properties.

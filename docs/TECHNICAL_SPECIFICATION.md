# Техническое задание
## Приложение для управления личными финансами (Finance Manager)

## 2.1. Общие положения	

### 2.1.1. Полное наименование системы и ее условное обозначение
Полное наименование: Программное приложение для управления личными финансами
Условное обозначение: Finance Manager (Финансовый Менеджер)

### 2.1.2. Основные сокращения, термины и определения	
- **GUI** - графический интерфейс пользователя
- **СУБД** - система управления базами данных
- **Доход** - финансовое поступление денежных средств
- **Расход** - затраты денежных средств
- **Бюджет** - запланированный лимит расходов по категории
- **Категория** - классификационная единица для доходов или расходов
- **Транзакция** - единичная операция дохода или расхода
- **MVC** - архитектурный паттерн Model-View-Controller

### 2.1.3. Наименования организации-заказчика
Индивидуальный заказчик для персонального использования

### 2.1.4. Календарный план работ
1. Анализ требований и проектирование: 1 неделя
2. Разработка основной функциональности: 2 недели
3. Тестирование и отладка: 1 неделя
4. Исправление ошибок и доработка: 1 неделя
5. Финальное тестирование и документирование: 1 неделя

### 2.1.5. Нормативные ссылки
- ISO/IEC 25010:2011 - Модель качества программного обеспечения
- Oracle Code Convention для Java

## 2.2. Назначения и цели создания системы

### 2.2.1. Назначение системы
Настольное приложение предназначено для учета, анализа и контроля личных финансов пользователя. Программа позволяет отслеживать доходы и расходы, устанавливать бюджеты по категориям и анализировать финансовое состояние с помощью визуальных графиков.

### 2.2.2. Цели создания системы
1. Обеспечение удобного и эффективного контроля личных финансов
2. Помощь в планировании бюджета и экономии средств
3. Сокращение времени на ведение личной финансовой отчетности
4. Повышение осознанности в отношении личных трат и доходов

### 2.2.3. Задачи создания системы
1. Разработка интуитивно понятного интерфейса для ввода и управления финансовыми данными
2. Создание системы учета доходов и расходов по категориям
3. Реализация возможности планирования бюджета и контроля его исполнения
4. Обеспечение визуального представления данных в виде диаграмм и графиков
5. Создание локальной базы данных для хранения финансовой информации пользователя

## 2.3. Требования к системе

### 2.3.1. Требования к функциональным характеристикам

#### 2.3.1.1. Требования к составу выполняемых функций
1. Учет доходов:
   - Ввод новых доходов с указанием категории, суммы, даты и описания
   - Редактирование и удаление существующих доходов
   - Категоризация доходов

2. Учет расходов:
   - Ввод новых расходов с указанием категории, суммы, даты и описания
   - Редактирование и удаление существующих расходов
   - Категоризация расходов

3. Бюджетирование:
   - Создание бюджетов по категориям расходов
   - Мониторинг исполнения бюджета
   - Отображение прогресса исполнения бюджета
   - Изменение существующих бюджетов

4. Управление категориями:
   - Создание новых категорий доходов и расходов
   - Редактирование названий категорий
   - Удаление неиспользуемых категорий

5. Статистика и аналитика:
   - Отображение общей суммы доходов, расходов и баланса
   - Генерация круговых диаграмм по категориям доходов и расходов

#### 2.3.1.2. Требования к организации входных данных
1. Данные для транзакций:
   - Дата (в формате ДД.ММ.ГГГГ)
   - Тип транзакции (доход/расход)
   - Категория
   - Сумма (положительное число с двумя знаками после запятой)
   - Описание (произвольный текст)

2. Данные для бюджетов:
   - Категория расходов
   - Сумма (положительное число с двумя знаками после запятой)

3. Данные для категорий:
   - Название (произвольный текст)
   - Тип (доход/расход)

#### 2.3.1.3. Требования к организации выходных данных
1. Список транзакций:
   - Табличное представление с колонками: дата, тип, категория, сумма, описание
   - Цветовое выделение доходов (зеленый) и расходов (красный)

2. Бюджеты:
   - Табличное представление с колонками: категория, сумма бюджета, потрачено, прогресс
   - Индикатор прогресса с процентным отображением

3. Категории:
   - Табличное представление с колонками: название, тип

4. Сводная информация:
   - Общий доход, общие расходы, баланс

5. Аналитика:
   - Круговые диаграммы распределения расходов и доходов по категориям

#### 2.3.1.4. Требования к взаимодействию с пользователем
1. Интерфейс на русском языке
2. Валюта - российский рубль (₽)
3. Графический интерфейс с вкладками для разных функций
4. Удобные формы для ввода данных с валидацией
5. Интуитивно понятные кнопки и элементы управления
6. Цветовое кодирование для визуального различения типов данных
7. Предупреждения и информационные сообщения при выполнении операций

### 2.3.2. Требования к нефункциональным функциям

#### 2.3.2.1. Требования к производительности
1. Время запуска приложения – не более 5 секунд
2. Время отклика на действия пользователя – не более 1 секунды
3. Объем локальной базы данных – не более 100 МБ при нормальном использовании
4. Возможность эффективной работы с не менее чем 10000 транзакций

#### 2.3.2.2. Требования к безопасности
1. Локальное хранение данных без передачи по сети
2. Отсутствие передачи персональных данных
3. Защита от SQL-инъекций при работе с базой данных

### 2.3.3. Требования к информационной и программной совместимости

#### 2.3.3.1. Требования к надежности
1. Валидация вводимых пользователем данных
2. Корректная обработка ошибок с понятными сообщениями
3. Предотвращение удаления категорий, используемых в транзакциях или бюджетах
4. Автоматическое создание резервной копии базы данных перед критическими операциями

#### 2.3.3.2. Требования к удобству использования (юзабилити)
1. Интуитивно понятный интерфейс с логичной организацией элементов
2. Визуальные индикаторы для статуса бюджетов (цветовые обозначения)
3. Наглядное представление статистики через круговые диаграммы
4. Контекстные подсказки для основных функций
5. Подтверждение операций удаления и изменения данных
6. Возможность быстрого ввода новых транзакций

#### 2.3.3.3. Требования к программной документации
1. Инструкции по установке и запуску приложения
2. Руководство пользователя с описанием всех функций
3. Документация по архитектуре и структуре кода
4. Описание тестовых сценариев

#### 2.3.3.4. Предварительный состав программной документации
1. README.md – общая информация о проекте, инструкции по установке
2. CREATE_EXE_GUIDE.md – руководство по созданию исполняемого файла
3. DEVELOPMENT.md – руководство для разработчиков
4. TEST_SCENARIOS.md – сценарии тестирования
5. JAVAFX_PROBLEMS.md – решение типичных проблем с JavaFX
6. NEW_FEATURES.md – описание новых функций
7. UML-диаграммы архитектуры приложения

#### 2.3.3.5. Специальные требования к программной документации
1. Документация должна быть написана на русском языке
2. Включение скриншотов интерфейса для иллюстрации функций
3. Документирование обработки ошибок и исключительных ситуаций
4. Включение примеров использования основных функций

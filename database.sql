-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.6.5-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for forum_database
CREATE DATABASE IF NOT EXISTS `forum_database` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `forum_database`;

-- Dumping structure for table forum_database.comments
CREATE TABLE IF NOT EXISTS `comments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `author_id` int(11) NOT NULL,
  `post_id` int(11) NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `date` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `comments_id_uindex` (`id`),
  KEY `comments_posts_fk` (`post_id`),
  KEY `comments_users_fk` (`author_id`),
  CONSTRAINT `comments_posts_fk` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
  CONSTRAINT `comments_users_fk` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1605 DEFAULT CHARSET=latin1;

-- Dumping data for table forum_database.comments: ~86 rows (approximately)
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
REPLACE INTO `comments` (`id`, `author_id`, `post_id`, `content`, `date`) VALUES
	(101, 37, 1, 'Здравей,\n\nБлагодаря, че писа. Ще видим какъв е проблемът.\n\nТодор', '2021-11-02 02:00:00'),
	(102, 3, 1, 'На мен ми прие решение с Java.', '2021-11-02 03:00:00'),
	(103, 34, 1, 'Те вчера го оправиха. Сега си работи без проблеми', '2021-11-02 04:00:00'),
	(201, 1, 2, 'Buddy Group One\n\nНие успяхме бързо да изговорим и да решим нещата за нашата група:\n\nИме: Augusta Legion (взимайки името от римски легион 1)\nСлоган: Veni, vidi, compilare (Дойдох, видях, компилирах - по наш груб превод)\nЛого:', '2021-11-16 02:00:00'),
	(202, 32, 2, 'Buddy Group 6:\n\nИме: The IT Crowd\nСлоган: Software is like a cathedral: first you build it, then you pray\nЛого:', '2021-11-16 03:00:00'),
	(203, 4, 2, 'Buddy Group 4:\nВъв връзка с четирилистната детелина.\n\nИме: The Lucky Ones\nСлоган: Luck is when Opportunity meets Preparation', '2021-11-16 04:00:00'),
	(204, 19, 2, 'Buddy Group 5:\n\nИме: d’LevelUppers\nСлоган: Life is too short for ordinary apps.\nЛого:', '2021-11-16 05:00:00'),
	(205, 20, 2, 'Buddy Group 3:\nИме: Cup ‘o Java\nСлоган: Programmer a tool for converting caffeine into code\nЛого:', '2021-11-16 06:00:00'),
	(206, 5, 2, 'Buddy Group 2:\n\nИме: Division By Zero\nСлоган: Impossible is nothing\nЛого:\n', '2021-11-16 07:00:00'),
	(301, 4, 3, 'Здравейте,\n\nРазрешихте ли проблема?\nЗащото може и без абстрактен клас.', '2021-11-30 02:00:00'),
	(302, 1, 3, 'Моето решение снощи бе да премахна създаването на нов EventLog в супер конструктора и оставих такъв само с дъщерните класове. Не съм сигурен дали точно така се очакваше да я решим и дали няма и по-добър вариант без абстрактни класове.', '2021-11-30 02:30:00'),
	(303, 3, 3, 'Дай идеи за без pls', '2021-11-30 03:00:00'),
	(304, 4, 3, 'Аз лично направих полето масива с Eventlogs - protected и си го пълня в child класа както в базовия.', '2021-11-30 03:30:00'),
	(305, 14, 3, 'И аз стигнах до същото решение', '2021-11-30 04:00:00'),
	(306, 3, 3, 'Общо взето май на това сме се спрели като решение и ние, но по този начин eventlog-a става уязвим към външни промени и вече не е енкапсулиран както преди', '2021-11-30 04:30:00'),
	(307, 21, 3, 'Относно бонус event-а който се записва при създаване на обект от тип Task, тъй като първо минаваме през конструктора на BoardItem:\n\nЗапазих опцията да се създава “Item created…” event при създаване на обект от тип BoardItem, тъй като може и да се наложи да се ползва в бъдеще. В конструктура на Task класа също създавам такъв event, но преди да се добави към history-то (history е името което избрах за листа в който пазя евентите), 1 ред отгоре, добавих history.remove(history.size()-1). По този начин, премахвам предишния евент, създаден при преминаването през BoardItem конструктура.\n\nОтносно промянта na access modifier-а от private на protected:\n\nРазделих класовете на пакети (пример: BoardItem, Task и Issue са в отделен пакет “item_types”), така че да не могат да бъдат достъпени и използвани методите/променливите там където не трябва.\n\nТова е подходът който избрах аз.\n\nEDIT:\nОказа се че има метод за чистене на лист - list name.clear()\nС него може да се избегне употребата на сетър за статус, като в конструктура на клас Task се извика advanceStatus() метода от BoardItem (Open > To Do). След това чистим лист-а и записваме “Item created…” event.', '2021-11-30 05:00:00'),
	(308, 4, 3, 'Уязвим е само откъм промени от child класове, което не мисля, че ще е проблем. Иначе @ivanm950 страхотно надграждане на задачата. Относно EventLog - може да ги запазваме просто в разлини EventLog и накрая да ги обединяваме спрямо типа?', '2021-11-30 05:30:00'),
	(309, 2, 3, 'Здравейте,\nВ условието и примерите (до колкото видях) не се налага да правим разграничение дали създавамe Task или Issue. В history-то все си пише Item. За сега като че ли е достатъчно само да минем през super конструктора, за да се запише като събитие.', '2021-11-30 06:00:00'),
	(310, 21, 3, 'Ако съм те разбрал правилно, пише си “Item created…” но проблема идва от реда на действията и статуса в скобите на събитието. Ако не се презапише евент-а при Task конструктура, излиза със статус “…[Open | ‘Due date’ ]” (от събитието в конструктура на BoardItem), вместо статус ‘To Do’, който променяме в Task конструктура.', '2021-11-30 06:30:00'),
	(311, 4, 3, 'Това нашата бъди група го реши с два конструктора в BoardItem и се вика super(title,dueDate,Status.TODO). Ако някой има друго решение, ще се радвам да го сподели.', '2021-11-30 07:00:00'),
	(312, 2, 3, 'И при мен е горе-долу както е oписал Чавдар. Items са в отделен пакет и този конструктор, дето приема Status е package private, а пък другия от версия 2 си е public.', '2021-11-30 07:30:00'),
	(401, 2, 4, 'Супер, тъкмо research-вам нещо свързано.\nНа въпроса от Java библията How do we achieve strong cohesion and loose coupling?\nДобър отговор ли е да кажем, че те са естествено следствие от спазване на SOLID принципте и максимата за programming to an interface?', '2021-12-01 02:00:00'),
	(402, 1, 4, 'Примера в първия линк с мухата и телемаркетъра е перфектен', '2021-12-01 03:00:00'),
	(403, 37, 4, 'Както всичко до сега, това е само част от верния отговор. :slight_smile: По-напред ще говорим за още принципи и похвати за постигане на strong cohesion и loose coupling. Ето добра 2 статия за нетърпеливите.\n\nP.S. Добавих тези ресурси 1, плюс още в learn системата.', '2021-12-01 04:00:00'),
	(501, 2, 5, 'Споделям и UnitTest (или по-точно опит за такъв, направен с гледане от другите две категории.) Може да си направите файл CreamImpl_Tests.java в com.telerikacademy.oop.cosmetics.tests.models с този source code и би трябвало да работи.\n\n\npackage com.telerikacademy.oop.cosmetics.tests.models;\n\nimport com.telerikacademy.oop.cosmetics.models.CreamImpl;\nimport com.telerikacademy.oop.cosmetics.models.enums.GenderType;\nimport com.telerikacademy.oop.cosmetics.models.enums.ScentType;\nimport com.telerikacademy.oop.cosmetics.tests.utils.TestData;\nimport org.junit.jupiter.api.Assertions;\nimport org.junit.jupiter.api.Test;\nimport org.junit.jupiter.params.ParameterizedTest;\nimport org.junit.jupiter.params.provider.ValueSource;\n\nimport static com.telerikacademy.oop.cosmetics.tests.utils.TestUtilities.initializeStringWithSize;\n\npublic class CreamImpl_Tests {\n\n    @ParameterizedTest(name = "with length {0}")\n    @ValueSource(ints = {CreamImpl.CREAM_MIN_LENGTH - 1, CreamImpl.CREAM_MAX_LENGTH + 1})\n    public void should_throwException_when_nameLengthOutOfBounds(int testLength) {\n        // Arrange, Act, Assert\n        Assertions.assertThrows(IllegalArgumentException.class, () ->\n                new CreamImpl(\n                        initializeStringWithSize(testLength),\n                        TestData.Cream.VALID_BRAND_NAME,\n                        TestData.POSITIVE_DOUBLE,\n                        GenderType.MEN,\n                        ScentType.LAVENDER));\n    }\n\n    @ParameterizedTest(name = "with length {0}")\n    @ValueSource(ints = {CreamImpl.CREAM_MIN_LENGTH - 1, CreamImpl.CREAM_MAX_LENGTH + 1})\n    public void should_throwException_when_brandNameLengthOutOfBounds(int testLength) {\n        // Arrange, Act, Assert\n        Assertions.assertThrows(IllegalArgumentException.class, () ->\n                new CreamImpl(\n                        TestData.Cream.VALID_NAME,\n                        initializeStringWithSize(testLength),\n                        TestData.POSITIVE_DOUBLE,\n                        GenderType.WOMEN,\n                        ScentType.ROSE));\n    }\n\n    @Test\n    public void should_throwException_when_PriceIsNegative() {\n        // Arrange, Act, Assert\n        Assertions.assertThrows(IllegalArgumentException.class, () ->\n                new CreamImpl(\n                        TestData.Cream.VALID_NAME,\n                        TestData.Cream.VALID_BRAND_NAME,\n                        -1,\n                        GenderType.MEN,\n                        ScentType.VANILLA));\n    }\n\n    @Test\n    public void should_create_Shampoo_when_ValidValuesArePassed() {\n        // Arrange, Act, Assert\n        Assertions.assertDoesNotThrow(\n                () -> new CreamImpl(\n                        TestData.Cream.VALID_NAME,\n                        TestData.Cream.VALID_BRAND_NAME,\n                        TestData.POSITIVE_DOUBLE,\n                        GenderType.MEN,\n                        ScentType.LAVENDER));\n    }\n\n}\n\n\nВъзможно е да съм допуснал бъг някъде, кажете ако откриете нещо подозрително.', '2021-12-01 02:00:00'),
	(502, 4, 5, '@tihomir.dimitrov.199 благодаря много за принта. Супер полезен е!\nМисля, че само трябва да се добавят и другите аромати към lavender - vanilla,rose.', '2021-12-01 03:00:00'),
	(503, 4, 5, '@Denislav.Vuchkov аз съм като досадната баба от квартала.\nНо остава само да се де-коментират два реда от public class TestData.\nИначе на мен мисля, че ми работят тестовете, за които нямаше дори да се сетя - та много благодаря, че сподели!', '2021-12-01 04:00:00'),
	(504, 2, 5, 'Да, Чавдаре, благодаря много за подсещането. Това го забравих, а е доста важно. В TestData трябва да се откоментира частта за Cream и този файл:\nCreateCreamCommand_Tests.java, трябва да изглежда по този начин:\n\npackage com.telerikacademy.oop.cosmetics.tests.commands;\n\nimport com.telerikacademy.oop.cosmetics.commands.CreateCreamCommand;\nimport com.telerikacademy.oop.cosmetics.core.CosmeticsRepositoryImpl;\nimport com.telerikacademy.oop.cosmetics.core.contracts.Command;\nimport com.telerikacademy.oop.cosmetics.core.contracts.CosmeticsRepository;\nimport com.telerikacademy.oop.cosmetics.models.contracts.Product;\nimport com.telerikacademy.oop.cosmetics.models.enums.GenderType;\nimport com.telerikacademy.oop.cosmetics.models.enums.ScentType;\nimport org.junit.jupiter.api.Assertions;\nimport org.junit.jupiter.api.BeforeEach;\nimport org.junit.jupiter.api.Test;\nimport org.junit.jupiter.params.ParameterizedTest;\nimport org.junit.jupiter.params.provider.ValueSource;\n\nimport java.util.List;\n\nimport static com.telerikacademy.oop.cosmetics.commands.CreateCreamCommand.EXPECTED_NUMBER_OF_ARGUMENTS;\nimport static com.telerikacademy.oop.cosmetics.tests.utils.TestData.POSITIVE_DOUBLE;\nimport static com.telerikacademy.oop.cosmetics.tests.utils.TestData.Cream.VALID_BRAND_NAME;\nimport static com.telerikacademy.oop.cosmetics.tests.utils.TestData.Cream.VALID_NAME;\nimport static com.telerikacademy.oop.cosmetics.tests.utils.TestUtilities.initializeListWithSize;\nimport static com.telerikacademy.oop.cosmetics.tests.utils.TestUtilities.initializeTestProduct;\n', '2021-12-01 05:00:00'),
	(505, 32, 5, '@tihomir.dimitrov.199\nред 12 от примерен изход,\nProduct doesn’t exist.\nслед като добавяме Creams в шопинг карт, не трябва ли да излезе\nProduct BestCream added to the shopping cart!', '2021-12-01 06:00:00'),
	(506, 32, 5, 'CreateShampoo MyMan Nivea 10.99 Men 1000 Every_Day\nCreateToothpaste White Colgate 10.99 Men calcium,fluorid\nCreateCream BestCream Nivea 10.99 Men Lavender\nCreateCategory Shampoos\nCreateCategory Toothpastes\nCreateCategory Creams\nAddToCategory Shampoos MyMan\nAddToCategory Toothpastes White\nAddToCategory Creams BestCream\nAddToShoppingCart MyMan\nAddToShoppingCart White\nAddToShoppingCart BestCream\nShowCategory Shampoos\nShowCategory Toothpastes\nShowCategory Creams\nTotalPrice\nRemoveFromCategory Shampoos MyMan\nShowCategory Shampoos\nRemoveFromCategory Toothpastes White\nShowCategory Toothpastes\nRemoveFromCategory Creams BestCream\nShowCategory Creams\nTotalPrice\nExit\n\nнов тест, с който всичко вече работи', '2021-12-01 07:00:00'),
	(507, 4, 5, 'Разцъках всички класове в Tests и се оказа че броят им е 54.\r\n\r\n', '2021-12-01 08:00:00'),
	(601, 32, 6, '(screenshot)', '2021-12-06 02:00:00'),
	(602, 2, 6, 'Здравей,\nАз го направих по този начин с допълнителна проверка в hasNext().\nТака няма нужда да се прави в print().\n\n        @Override\n        public boolean hasNext() {\n            return currentIndex < elements.length && elements[currentIndex] != null;\n        }', '2021-12-06 03:00:00'),
	(603, 4, 6, 'Преформатирах си цялата задача, защото мислех, че проблемът е от другъде.\nНакрая се оказа че трябва да променя итератора.\nПечата приложеното при този код в Main (исках да видя дали от мейна ще печата правилно, което беше така):\n\nSystem.out.println();\n        for (String s : list) {\n            System.out.print(s + " ");\n        }\nScreenshot 2021-12-07 at 2.30.22\nScreenshot 2021-12-07 at 2.30.22\n1946×1080 242 KB\nКато променя принта на:\n@Override\npublic void print() {\n\n        for (T e : this) {\n            System.out.print(e + " ");\n        }\n    }\nвлиза в овъррайднатия итератор, което не разбирам защо не работи с “elements”.', '2021-12-06 04:00:00'),
	(604, 2, 6, 'Ако го направиш така: for (E element : elements)\nВсъщност итерираш, изпозлвайки вградения forEach на Array class (elements е обикновен масив).\n\nЗа това се налага да го дефинираш по другия начин: for (E element : this)\nТака написано задаваш, че искаш да се позлва новия итератор (this = MyListImpl).', '2021-12-06 05:00:00'),
	(701, 37, 7, 'Здравей,\n\nотиди в Project Settings -> Modules -> Dependencies\n\nimage\nimage\n1100×695 25.1 KB\nimage\n\nНатисни плюсчето и избери JARs or Directories. След това навигирай до lib папката, която върви с проекта. В нея са файловете, който се използват за изпълнение на тестовете, а твоят проект, не знае къде се намират по някакви причина.\n\nЩе направя темата публична, защото може още някой да има този проблем :slight_smile:\n\nТодор', '2021-12-07 02:00:00'),
	(702, 20, 7, 'Уж тръгна за момент но в следващия момент половината се счупиха но поне ми дава да ги стартирам сега', '2021-12-07 03:00:00'),
	(703, 37, 7, 'А import-нал ли си Test в класа?\n\n', '2021-12-07 04:00:00'),
	(704, 20, 7, 'Отказах се от тази каша и реших да направя нов проект на чисто,но пък сега ми дава проблем с пакета', '2021-12-07 05:00:00'),
	(705, 37, 7, 'Тук вече проблемът в с имплементацията на MyListImpl.', '2021-12-07 06:00:00'),
	(706, 20, 7, 'Мерси Тоше,оправих се вече!\n', '2021-12-07 07:00:00'),
	(801, 15, 8, 'Само мога да кажа, че не си сама :slight_smile:', '2021-12-07 02:00:00'),
	(802, 37, 8, 'Здравей, Софи,\n\nопитай да отидеш в Project Structure (ctrl + alt + shift + s) и избери Modules -> Sources, маркирай папакта с тестовете и избери Tests.\n\nimage\nimage\n983×428 22.1 KB\nТодор', '2021-12-07 03:00:00'),
	(803, 1, 8, 'Здрасти Софи,\n\nПри мен имаше същия проблем и го оправих когато с десен бутон върху папката тест отидох на Mark Directory as --> Test Sources Root.\n\nСъщо помага ако провериш че имаш правилната версия на SDK от File --> Project Structure.\n\nПонякога програмата има нужда и от File --> Invalidate Caches.\n\nНадявам се някое от тези да помогнат', '2021-12-07 04:00:00'),
	(804, 33, 8, 'Благодаря, сега работи!', '2021-12-07 05:00:00'),
	(805, 20, 8, 'При мен беше така също но нещо се счупи тотално…!Дори пробвах съветите от поста но пак не става\n\nScreenshot (1)\nScreenshot (1)\n1920×1080 310 KB\n\n\n\n', '2021-12-07 06:00:00'),
	(806, 15, 8, 'При мен и двата предложени метода по-горе не сработват.\nimage\nimage\n740×970 46.7 KB\n\nНяма възможност да пусна тестовете.\nТова, че са ми в червено класовете предполагам е нещо от гит и не би трябвало да влияе?', '2021-12-07 07:00:00'),
	(807, 20, 8, 'Промени си теста на Test source Root може от това да е', '2021-12-07 08:00:00'),
	(808, 15, 8, 'Пробвах, не стана. Свалих като архив целия проект отначало и сега всичко изглежда добре. Ще прехвърля кода в новия файл.', '2021-12-07 09:00:00'),
	(809, 20, 8, 'Точно това направих и аз току що', '2021-12-07 10:00:00'),
	(901, 2, 9, 'Здравей,\nМоже да пробваш показанато в това видео 3 oколо 2:50:00 (от # Alpha 34 Java - Object-Oriented Programming => ## Workshop 2 Discussion).', '2021-12-12 02:00:00'),
	(902, 15, 9, 'Това съм го направил още след сесията, но не помага в случая. Странно, защото вади същата грешка. :roll_eyes: :thinking:', '2021-12-12 03:00:00'),
	(1001, 4, 10, 'В почти всичките методи на TaskManagementRepositoryImpl.\nЕто един пример + дженерик:\n\npublic <T extends Identifable> T findItemById(List<T> items, int id){\n    return items\n            .stream()\n            .filter(item -> item.getId() == id)\n            .findFirst()\n            .orElseThrow(() -> new ElementNotFoundException(\n                    String.format("No %s with id %d",items.getClass().getSimpleName(), id)));\n}\nНа мен обаче все още не ми е много ясно какво е точно Optional.', '2021-12-20 02:00:00'),
	(1002, 37, 10, 'Много готин пример :slight_smile:\n\nИначе за Optional:\n\nclass Person {\n    private String name;\n\n    public String getName() {\n        return name;\n    }\n}\n\nPerson person = null; \nperson.getName();\nТова няма да спре програмата да се компилира, нали така? Чак когато се изпълни person.getName() ще получим NullPointerException. За да се предпазим от това трябва да проверим дали person не е null преди да използваме някой от методите му. Optional е клас, който ни позволява да премахнем проверките за това дали нещо е null или по-скоро да ги интегрираме по-елегатно в кода.\n\n“Java 8 introduced a type Optional<T> , which can be used to handle potentially missing values. It does so by wrapping a reference (which might be null) and providing some nice methods to interact with the value in case it’s present.” - Source\n\nВ твоя пример .findFirst() ни връща Optional<Identifiable>. Ако има елемент, който да отговаря на filter-а, findFirst() ще върне Optional със стойност елемента. Ако няма, findFirst() ще върне празен Optional.\n\nМоже да прочетеш повече тук 2. Ето и една извадка 3 от книгата Moden Java in Action по въпроса какъв проблем решава Optional.', '2021-12-20 03:00:00'),
	(1003, 20, 10, 'Здравейте,споделям един по-сложен пример за използване на streama .Тошето ни помогна разбира се и мисля че сега усетихме каква мощ има всъщност.\n\nScreenshot (9)', '2021-12-20 04:00:00'),
	(1004, 37, 10, 'Важно е да отбележим, че използвахме flatMap(), a не map(). flatMap() “изглажда” вложени колекции:\n\nList<List<Integer>> result = Stream.of(Arrays.asList(1, 2), Arrays.asList(3, 4))\n  .collect(Collectors.toList()); // [[1, 2], [3, 4]]\n\nresult\n    .stream()\n    .flatMap(list -> list.stream())\n    .collect(Collectors.toList()); // [1, 2, 3, 4]\nОще информация тук 2 и тук 1.', '2021-12-20 05:00:00'),
	(1101, 32, 11, 'Още един въпрос в тази насока. Отключи ни се survey 2 в ООP модула, кога трябва да го попълним? Струва ми се, че е някаква стара анкета, защото трябва да дадем feedback за Tomislav Rashkov, а той не ни е водил, както и за трейнърите Влади и Виктор, с които сме имали само една лекция.', '2022-01-03 02:00:00'),
	(1102, 37, 11, '@Саша - Това са просто placeholder срещи. Ние ще ви звъннем в часа от графика, който публикувах.\n\n@Иво - благодаря, ще го оправим.', '2022-01-03 03:00:00'),
	(1201, 12, 12, 'Мен и за двете последни задачи ми дава някакъв time limit exceeded или short circut, а в интелиджей всичко работи… И ми е много интересно какъв е този time limit, deto 0.3s са му много', '2022-01-13 02:00:00'),
	(1202, 32, 12, '(meme)', '2022-01-13 02:30:00'),
	(1203, 33, 12, 'Здравей,\nразгледах кода ти и не бях сигурна следната проверка какво прави(line 45):\n\n if (position == pokemonList.size()) {\n    pokemonList.add(pokemon);\n} else {\n    pokemonList.add(position - 1, pokemon);\n}\nзатова просто я смених на:\npokemonList.add(position - 1, pokemon);\n\nи сега кода ти връща 70т/80т като се чупи само на " Time Limit Exceeded"', '2022-01-13 03:00:00'),
	(1204, 2, 12, 'Леле… Благодаря ти много, Софи! Как го хвана това…\n\nИдеята му беше като се подадат 3 неща и размера е 2, да слага в края. Например:\nadd Charmander Fire 33 1\nadd Squirtle Water 22 2\nadd Jigglypuff Fairy 30 3\n\nМислех си, че ще даде exception, ако на List с размер 2 му кажа add(2,нещоси). Излгежда че, съм грешал.\n\n@emiliyana.kalinova - и при мен стана така с първата. Дори си направих решението само да записва данните в 3 Map-а и после hardcode-нах да принтира верния отговор на първия тест. Тоест без да прави никакви други операции, просто ги слагам в Map и принтирам верния стринг - пак ми даде time-out… Затова изтрих всичко, отихдох в google и намерих някакъв индийски метод, дето брои chars само с масив и го адаптирах за моята цел. :sweat_smile: И сега не ползвам никакви DSA-та и други такива лигавщини и имам 100/100. Малко се чудих дали не се измести леко акцента на упражението, ама после си спомних, че прекалено много размисъл не винаги е за добро. :innocent:', '2022-01-13 03:30:00'),
	(1205, 2, 12, 'Снощи до късно пробвахме разни неща с @dimievivo и @martiparti166 и успяхме да качим по 10-ина точки на решенията. Сега, когато Марто видя отговора на Софи му дойде идеята да сменим:\n\npublic static final Map<String, List> pokemonByType = new HashMap<>();\n\nна\n\npublic static final Map<String, TreeSet> pokemonByType = new HashMap<>();\n\nи така минават всички тестове. :slight_smile: Благодаря ти, Марто!\n\nP.S. По принцип с такъв сетъп, ако имаме два еднакви Pokemon-а, мисля, че ще се показва само един, но явно няма такъв сценарий в Judge. Tова може би трябва да се уточни в условието. Защото сега пише само, че може да не са unique.', '2022-01-13 04:00:00'),
	(1206, 22, 12, 'Емили, имах абсолютно същата драма(на 1ва)- накрая просто замених проверката за най- голяма стойност, която бях направила с преобразуване на мапа в сет и stream(), с прост for цикъл и if-else…и то взе, че мина всички теастове (след като прочетох пак, че се иска по-малкото ascii, а не по-голямото). Иначе казано KISS и си четете внимателно условията', '2022-01-13 04:30:00'),
	(1207, 12, 12, 'Супер, само дето като учим ДСА очаквам да работи с нея, а не да трябва да се мъдри някакво друго вярно решение, което judge системата да приеме, според мен всичко се обезсмисля по този начин.', '2022-01-13 05:00:00'),
	(1208, 22, 12, 'аз ползвам DSA (3 броя HashMap) (https://pastebin.com/jMe9MMgB 3), просто операциите в .stream() се оказаха не най-оптималните откъм време, защото с тях няколко пъти ми обхождаше целия мап, а с простия фор го обхождам 1, за да намеря най-голямата стойност.\nиначе аз също бях малко кисела, като си мислех, че ще трябва да прибегна до масивите, но\nStream() не е от DSA модула, така че можеш да не го ползваш', '2022-01-13 05:30:00'),
	(1209, 5, 12, 'И аз много се мъчих на първата вчера. Днеска със свежа глава, му озари идеята, че ако ползвам по 2 hashmapа за upper, small и symbol също става. В единия пълня Char и итерирам повторенията, а втория помощен като за key ползвам повторения на съответния символ от първия и запазвам symbolа като value, като трики момента е че ако вече има символ вътре правя Math min между това което е вътре и current symbol cast to char. Накрая взимаш от помощния кой е най - големия key и готово. Днеска го разписах и worked like a charm.\n\n', '2022-01-13 06:00:00'),
	(1210, 12, 12, 'Моето решение е с един Hashmap в който слагам всички символи от 32 до 127 - тоест special characters, lower & upper case letters. След това създавам 3 map-a чрез streams за всяка категория. Ето го кода: https://pastebin.com/A3GKTj1C 2\n\n', '2022-01-13 06:30:00'),
	(1211, 8, 12, 'Супер готини неща сте направили. Браво! Аз от “богатия” си :smile: няколко месечен опит с Judge, вече знам, че няма смисъл да правиш комплексен код, а най-важното е това което правиш, да работи бързо, съответно и с малко ресурс. Иначе не минава и започваш да си губиш времето в експерименти.\nТа с тази презумция, направих поредното си първобитно решение - никакви колекции и никакви стриймове. Един чист масив от 127 нули. В един цикъл извъртам всички символи, взимам ascii стойността и за този индекс увеличавам стойността с единица. Така получавам масив от 127 числа, които показват колко бройки има за всяка ascii стойност. От там нататък вече си извличам за съответните интервали. Красивото в случая е, че кърти всички тестове за 0,2 сек\n:smile:\nkiss', '2022-01-13 07:00:00'),
	(1212, 32, 12, 'За пореден път наказваш Judge! Много интересн подход.\nСправедливостта възтържествува :man_judge:\n\n', '2022-01-13 07:30:00'),
	(1301, 8, 13, 'Вече работи, но ще ми е супер интересно някой от @Team да разкаже какъв беше проблема и как успяхте да го разрешите.', '2022-02-10 02:00:00'),
	(1302, 39, 13, 'Здравейте, проблемът е отстранен, извиняваме се за причиненото неудобство.\n\n', '2022-02-10 03:00:00'),
	(1303, 39, 13, 'Относно естеството на проблема, имаше грешна конфигурация в reverse proxy-то и един конкретен адрес не се обработваше от правилния сървър и сътветно не можеше да се логвате.\n\n', '2022-02-10 04:00:00'),
	(1304, 8, 13, 'А как открихте проблема? Кой го откри, кой го поправи… дайте малко повече инфо от кухнята.\n\n', '2022-02-10 05:00:00'),
	(1305, 39, 13, 'Днес правихме промени по логина в една от вътрешните системи, при която беше направена въпросната грешна конфигурация. След като видях, че въпросният адрес връща 404 грешка, нямаше много чудене от какво може да е породено. Когато сам сгрешиш е най-лесно да си намериш после грешката :wink:\n\n', '2022-02-10 06:00:00'),
	(1306, 40, 13, 'Лирическо отклонение ala Soft Skills:\nЕто на това му казвам прогрес в развиването на growth mindset: щом и на проблема вече се гледа като на възможност за учене :).\n\n', '2022-02-10 07:00:00'),
	(1401, 2, 14, 'Много добър въпрос, Иво. :slight_smile: Ще следя с интерес.\nВ допълнение искам да попитам дали това по някакъв начин е лоша парктика и ако да, как е по-правилно да се направи?\n\n    @Override\n    public Post delete(int id) {\n        Post post = getById(id);\n        post.getComments().forEach(entry -> removeComment(id, entry.getId()));\n        post.getLikes().clear();\n        update(post);\n        try (Session session = sessionFactory.openSession()) {\n            session.beginTransaction();\n            session.delete(post);\n            session.getTransaction().commit();\n        }\n        return post;\n    }\n\n    @Override\n    public Comment removeComment(int postID, int commentID) {\n        Comment comment = findComment(postID, commentID);\n        try (Session session = sessionFactory.openSession()) {\n            session.beginTransaction();\n            session.delete(findComment(postID, commentID));\n            session.getTransaction().commit();\n        }\n        return comment;\n    }\n', '2022-02-23 02:00:00'),
	(1402, 41, 14, 'Здравейте,\n\nТова, което ви казах е, че не е добре да имате каскадни операции в базата.\nТ.е. не е добре ако триете запис в базата да сте активирали каскадно да се изтрие всичко каквото е нужно, за да си запази интегритета.\n\nКакво можем да направим?\n\nДа използваме “софт” изтриване - т.е. флаг isDeleted.\nПредимства: Не е необходимо каскадно да се трие каквото и да било. Например след изтриване на юзър той не може да се логва (понеже го “няма”), но ако има постове и коментари те си стоят и са с неговите данни.\nТрудности:\nлистването става една идея по сложно - трябва да всеки път да имате предвид и взимате само “не-изтритите” записи.\nИма да се помисли и реши какво става с уникалността на имената - т.е. какво става ако искате да регистрирате нов юзър с юзърнейм дето е изтрит?\nРазлични ситуации породени от това, че хем го “няма” хем го “има” записа. В повечето случаи е въпрос на някакво решение какво да се случва в конкретния сценарии.\nХард изтриване - изтриване на записа от базата данни.\nПредимства: Веднъж изтрито нещо - няма го напълно. няма усложнения около листване, менажиране на нови записи и т.н.\nТрудности: Да се направи добре. Има да се помисли:\nКак да се направи операцията и да се запази интегритета на данните? (без каскадни операции в базата). Най-простият вариант е в бизнес логиката да се погрижите да изтриете всичко, което е нужно (свързано с конкретния запис). Например ако постовете задължително имат създател (не може да е null) и искате да изтриете юзър - това означава, че трябва да изтриете всички постове на този юзър, което означава, че трябва да изтриете всички коментари към тези постове също! Това е доста радикално и препоръчвам да сте много сигурни, че това искате да направите! (И ако го правите правете го в сървис-а, за да ви е пред очите)\nВариант е постовете/коментарите да останат, но да пише unknown user/deleted user. Помислете как бихте могли да реализирате това :wink:\nНа въпроса на Денис - при хард изтриване - бих сложил логиката в сървиса.', '2022-02-23 03:00:00'),
	(1403, 32, 14, 'А може ли да се комбинират двата варианта, при по-лесните неща ( да се изтрие телефонен номер от една единствена таблица, като няма други усложнения) да ползваме hard delete, а примерно като искаме да изтрием юзър, но не искаме да се загуби ценната информация, която е слагал до сега във форума, му сменяме isDeleted полето ( още не сме стигнали до там) на true, и има една единствена проверка в бизнес логиката, и ако се опитваме да му отворим профила примерно ще излиза че е изтрит? Питам, защото никога не съм виждал как работи front end, и не знам дали това е възможно.', '2022-02-23 04:00:00'),
	(1404, 41, 14, 'Възможно е да ги комбинирате. Водещо обаче не бива да е “дали е лесно”, а доколко има смисъл за проблемите дето решава вашия апп :wink:', '2022-02-23 05:00:00'),
	(1501, 1, 15, 'Привет, Саша\n\nАз се помъчих с това по-рано тази седмица и има два варианта\n\nПрез сайта на swagger можеш да си изградиш документацията но там е по-бавно и един по един си пишеш всичките REST APIs. Плюса е че имаш повече контрол над процеса\n\nВариант номер две е да си направиш конфигурация за swagger в кода си която директно ще прочете и генерира цялата ти документация. За да се подкара това ти трябват три неща:\n2.1 Dependency → implementation ‘io.springfox:springfox-boot-starter:3.0.0’\n2.2 Application properties → spring.mvc.pathmatch.matching-strategy = ANT_PATH_MATCHER\n2.3 Клас в който да конфигурираш документацияата:\n\nimage\nimage\n1027×843 71.4 KB\nРезултата който се получава е ето тази страница :slight_smile:\n\nimage\nimage\n1756×828 31.5 KB', '2022-03-04 02:00:00'),
	(1502, 2, 15, 'Привет,\n\nБлагодаря на Тишо, че подкара това в нашия проект и аз буквално нищо не трябваше да правя. :smiley:\n\nЕдин малък ъпдейт все пак да отчета принос. С кода по-долу като добавка към метода Docket ще се появи и опцията за Authentication в Swagger демотата. Вътре трябва да се попълни валиден username и работи.\n\n    @Bean\n    public Docket api(){\n        return new Docket(DocumentationType.SWAGGER_2)\n                .select()\n                .paths(PathSelectors.any())\n                .apis(RequestHandlerSelectors.basePackage("com.forum.javaforum"))\n                .paths(regex("/api.*"))\n                .build()\n                .apiInfo(apiDetails())\n                .securityContexts(List.of(securityContext()))\n                .securitySchemes(List.of(apiKey()));\n\n    }\n\n    private ApiKey apiKey() {\n        return new ApiKey("JWT", "Authorization", "header");\n    }\n\n    private SecurityContext securityContext() {\n        return SecurityContext.builder().securityReferences(defaultAuth()).build();\n    }\n\n    private List<SecurityReference> defaultAuth() {\n        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");\n        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];\n        authorizationScopes[0] = authorizationScope;\n        return List.of(new SecurityReference("JWT", authorizationScopes));\n    }', '2022-03-04 03:00:00'),
	(1601, 37, 16, 'Каква беше главната причина да искаме да върнем копие на колекцията?\n\n\n\n\n', '2021-12-16 02:00:00'),
	(1602, 1, 16, 'Да предотвратим нежелан достъп до реалния обект и промени по него.\n\nОсвен да пробвам да направя промяна върху върнатата колекция и да видя дали това променя реалната? Ще изпробвам сега.', '2021-12-16 03:00:00'),
	(1603, 37, 16, 'Сподели резултата след това :slight_smile:\n\n\n\n\n', '2021-12-16 04:00:00'),
	(1604, 1, 16, 'Станаха нещата:\n\n@Test\npublic void getCategories_should_returnCopyOfCollection() {\n    List<Category> categoryList = productRepository.getCategories();\n    categoryList.add(new CategoryImpl(validCategoryName));\n\n    Assertions.assertEquals(0, productRepository.getCategories().size());\n}\n\n@Test\npublic void getProducts_should_returnCopyOfCollection() {\n    List<Product> productList = productRepository.getProducts();\n    productList.add(new ProductImpl(validProductName, validBrandName, Double.parseDouble(validPrice),\n            GenderType.valueOf(validGenderType.toUpperCase())));\n\n    Assertions.assertEquals(0, productRepository.getProducts().size());\n}', '2021-12-16 05:00:00');
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;

-- Dumping structure for table forum_database.phone_numbers
CREATE TABLE IF NOT EXISTS `phone_numbers` (
  `user_id` int(11) NOT NULL,
  `phone_number` varchar(32) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `phone_numbers_phone_number_uindex` (`phone_number`),
  UNIQUE KEY `phone_numbers_user_id_uindex` (`user_id`),
  CONSTRAINT `phone_numbers_users_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table forum_database.phone_numbers: ~2 rows (approximately)
/*!40000 ALTER TABLE `phone_numbers` DISABLE KEYS */;
REPLACE INTO `phone_numbers` (`user_id`, `phone_number`) VALUES
	(1, '0876051465'),
	(2, '0876051467');
/*!40000 ALTER TABLE `phone_numbers` ENABLE KEYS */;

-- Dumping structure for table forum_database.photos
CREATE TABLE IF NOT EXISTS `photos` (
  `user_id` int(11) NOT NULL,
  `photo` text NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `photos_user_id_uindex` (`user_id`),
  CONSTRAINT `photos_users_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table forum_database.photos: ~3 rows (approximately)
/*!40000 ALTER TABLE `photos` DISABLE KEYS */;
REPLACE INTO `photos` (`user_id`, `photo`) VALUES
	(1, 'http://res.cloudinary.com/tddvjavaforum/image/upload/v1648025851/TDDVForumPictures/ProfilePictures/userID:1.jpg'),
	(3, 'http://res.cloudinary.com/tddvjavaforum/image/upload/v1647854891/TDDVForumPictures/ProfilePictures/userID:3.jpg'),
	(5, 'http://res.cloudinary.com/tddvjavaforum/image/upload/v1647525524/TDDVForumPictures/ProfilePictures/userID:5.jpg');
/*!40000 ALTER TABLE `photos` ENABLE KEYS */;

-- Dumping structure for table forum_database.posts
CREATE TABLE IF NOT EXISTS `posts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `author_id` int(11) NOT NULL,
  `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `date` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `posts_id_uindex` (`id`),
  KEY `posts_users_id_fk` (`author_id`),
  CONSTRAINT `posts_users_id_fk` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1223 DEFAULT CHARSET=latin1;

-- Dumping data for table forum_database.posts: ~17 rows (approximately)
/*!40000 ALTER TABLE `posts` DISABLE KEYS */;
REPLACE INTO `posts` (`id`, `author_id`, `title`, `content`, `date`) VALUES
	(1, 34, 'Задачата “Добри числа” не приема Java input', 'Задачата “Добри числа” работи само със C++ и не приема Java input.', '2021-11-02 01:00:00'),
	(2, 38, 'Dream Buddy Group', 'Здравейте,\r\n\r\nПравя тази тема с насоки за домашното, което имате по buddy групи.\r\n\r\nПреди домашното обаче 2 важни неща:\r\n\r\nПърво, моля ви да си качите снимки в teams, Learn и тук във форума , на която да сте вие и да се виждате добре. По възможност качвайте една и съща снимка за трите платформи.\r\nВторо, напомням отново, че официалният канал за комуникация между нас е тук, във форума . Ако имате някакво питане или искате да споделите нещо - пишете тук, тагвайки хората, чието внимание искате да ангажирате с дадената тема.\r\nА сега задачата ви:\r\n\r\nPredictive Index Profiles\r\n\r\nКакто говорихме, трябва да сте получили на личните ви мейли вашите профили според платформата на Predictive Index. (Ако някой все още няма профила си да ми пише :slight_smile: )\r\n\r\nЗадачата ви е на следващта buddy group среща да представите един на друг профилите си. Прочетете ги първо сами за себе си и помислете как да ги представите накратко на вашите съотборници. Може да обсъдите с кои от нещата отбелязани там сте съгласни и има ли такива, с които не сте. Напомням, че по-натам в програмата ще говорим за профилите ви. За момента целта е да се запознаете малко повече с нуждите на екипа си в работен контекст.\r\n\r\nDream Buddy Team\r\n\r\nИзмислете име, слоган и лого на buddy групата. Имайте предвид, че това са важни елементи на вашата група, които ще може да си ползвате и като правите общи презентации. Те няма да са видими като информация за външни партньори/лица, но ще си ги ползваме много активно самите ние до края на програмата :slight_smile:\r\n\r\nВажно :\r\n\r\nЕдин човек от buddy група да се ангажира да публикува отдолу в коментар домашното.\r\nКраен срок: 23.11 / 10:00 ч.\r\nАко имате въпроси, питайте смело :slight_smile:\r\n\r\nОще нещо! Ще имате начало на менторска програма на 22.11 от 20 ч (тогава ще са срещите с менторите на Buddy групите. Просто си запазете този слот за кратка среща онлайн. Ще ви пиша допълнително\r\n\r\nУспех,\r\nБоян', '2021-11-16 01:00:00'),
	(3, 1, 'BoardR - Part 3 - How to get around the EventLog issue', 'Здравейте колеги,\n\nНашата група снощи дискутира известно време как може да се справим със следния проблем на BoardR част 3-та:\n\nУсловието иска от нас когато се създаде обект от тип Task or Issue да въведем това в историята. Проблема който ние имахме е че следвайки предните части на BoardR, при създаването на обект от тип BoardItem също трябва да се създава въведение в историята и това го правихме в конструктора\nПри инициализирането на дъщерния клас реално имахме проблем че два пъти добавяме в историята\nСлед дискусии и насоки от @a.o.velikova стигнахме до следните 2 заключения:\n\nПри проверка на валидността на assignee в конструктора на Task програмата хвърля exception и там спира своето изпълнение. Това като стане до колкото разбрах не се добавя това което е добавено в историята в супер конструктора\nAко не отпечатваме директно EventLogs на BoardItema, a запазваме историята в ArrayList, не ни трябва абстрактен клас защото при хвърлянето на exception конструктора изтрива новосъздадения ArrayList в базовия конструктор и го няма вече\nВероятно това може да се реши и с правенето на BoardItem абстрактен клас (което ще учим днес). Когато BoardItem е абстрактен той сам по себе си не може да се инициализира и неговия конструктор няма да добавя в историята и ще се добавя само в Task and Issue', '2021-11-30 01:00:00'),
	(4, 37, 'Programming to an Interface', 'Здравейте,\n\nсподелям няколко ресурса по темата защо List list = new ArrayList() е по-добре от ArrayList list = new ArrayList().\n\nWhat does it mean to “program to an interface”? 6 - различни гледни точки в stackoverflow\nWhat Does It Mean to Program to Interfaces? 4 - малко по-подробна статия от Baeldung\nProgramming to an Interface: A Simple Explanation 1 - пример със C#, но обяснението е добро\nUnderstanding “programming to an interface” 2 - друга дискусия, този път в software engineering stackexchange-a\nИ вие сте добре дошли да споделите ресурси или пък как вие разбирате темата :slight_smile:\n\nТодор', '2021-12-01 01:00:00'),
	(5, 1, 'Comestics Part 2 - Week 3', 'Здравейте,\n\nАз реших да си напиша и примерен вход за да си тествам програмата и след допълнителната задача.\n\nРеших да го споделя за да спестя време на други:\n\nПримерен вход:\n\nCreateShampoo MyMan Nivea 10.99 Men 1000 Every_Day \nCreateToothpaste White Colgate 10.99 Men calcium,fluorid \nCreateCream BestCream Nivea 10.99 Men Lavender \nCreateCategory Shampoos \nCreateCategory Toothpastes \nCreateCategory Creams \nAddToCategory Shampoos MyMan \nAddToCategory Toothpastes White \nAddToCategory Creams BestCream \nAddToShoppingCart MyMan \nAddToShoppingCart White \nAddToShoppingCart Creams \nShowCategory Shampoos \nShowCategory Toothpastes \nShowCategory Creams \nTotalPrice \nRemoveFromCategory Shampoos MyMan \nShowCategory Shampoos \nRemoveFromCategory Toothpastes White \nShowCategory Toothpastes \nRemoveFromCategory Creams BestCream \nShowCategory Creams \nTotalPrice \nExit \nПримерен изход:\n\nShampoo with name MyMan was created! \nToothpaste with name White was created! \nCream with name BestCream was created! \nCategory with name Shampoos was created! \nCategory with name Toothpastes was created! \nCategory with name Creams was created! \nProduct MyMan added to category Shampoos! \nProduct White added to category Toothpastes! \nProduct BestCream added to category Creams! \nProduct MyMan was added to the shopping cart! \nProduct White was added to the shopping cart! \nProduct doesn\'t exist. \n#Category: Shampoos\n#MyMan Nivea \n#Price: $10.99 \n#Gender: Men \n#Milliliters: 1000 \n#Usаge: EveryDay \n=== \n#Category: Toothpastes \n#White Colgate \n#Price: $10.99 \n#Gender: Men \n#Ingredients: [calcium, fluorid] \n=== ', '2021-12-02 01:00:00'),
	(6, 4, 'Collections & Generics In-Class Activity Submission', 'Здравейте колеги,\n\nОт известно време мъча този итератор, но имам чувството че не го override-вам, въпреки че всичко ми изглежда ок и вече не съм сигурен дори какво да търся.\nПроблемът е че принтва и нулите до края на Capacity, ако я няма тази проверка в принта:\n\n“Cat Dog Dog null”\n\nМоже ли да погледнете:\n> @Override\n\n    public void print() {\n\n        for (T e : elements) {\n            //if (e != null){\n                System.out.print(" " + e);\n            //}\n        }\n    }\n\n    @Override\n    public Iterator<T> iterator() {\n        return new MyArrayListIterator();\n    }\n    private class MyArrayListIterator implements Iterator<T> {\n        private int currentIndex = 0;\n\n        @Override\n        public boolean hasNext() {\n            return currentIndex < size();\n        }\n\n        @Override\n        public T next() {\n            T result = elements[currentIndex];', '2021-12-06 01:00:00'),
	(7, 15, 'IntelliJ Test Issues - `org.junit.jupiter.api` does not exist', 'Тоше здравей,имам проблем с проекта в InteliJ тъй като тестовете нещо се трошат незнайно защо и не мога да си ги отворя.Отварях проекта няколко пъти за да съм сигурен че го отварям от последната папка Skeleton и всичко изглежда ок но като цъкна тестовете не ми да да ги стартирам.', '2021-12-07 01:00:00'),
	(8, 33, 'Tests за Collections and Generics In-Class Activity', 'Здравейте,\nпоради някаква причина не могат да ми тръгнат тестовете на задачата. Получвам следното съобщение: “Java file outside of source root”. Пробвах да го отворя по най-различни начини и не се оправя. Някой може ли да удари едно рамо?', '2021-12-08 01:00:00'),
	(9, 15, 'Workshop3 Assessment - Homework on Java 15', 'Привет!\nЕдно от подадените ми домашни за оценяване е правено с java 15, а аз работя с java 11 както беше препоръчано в началото. При опит да пусна тестовете получавам съобщение “java: error: release version 15 not supported”.\nИма ли начин да преобразувам проекта в 11 версия, за да го тествам?\nЗа сега ще submit-на оценка само на база четене на кода.', '2021-12-12 01:00:00'),
	(10, 37, 'Stream API is cool!', 'Здравейте,\n\nСподелям един по-сложен пример за използването на .map(), понеже този от лекцията беше по-опростен:\n\nМетодът:\n\n@Override\npublic String printVehicles() {\n    StringBuilder sb = new StringBuilder(String.format("--USER %s--\\n",userName));\n    if (vehicles.size() > 0) {\n        for (int i = 0; i < vehicles.size(); i++) {\n            sb.append(String.format(\n                    "%s:\\n",vehicles.get(i).getType().toString()));\n            sb.append(vehicles.get(i).toString());\n        }\n    } else {\n        sb.append("--NO VEHICLES--");\n    }\n\n    return sb.toString();\n}\nМоже да бъде пренаписан без индекси така:\n\n@Override\npublic String printVehicles() {\n    String result = String.format("--USER %s--\\n", userName);\n\n    if (vehicles.isEmpty()) {\n        return result + "--NO VEHICLES--";\n    }\n\n    return result + vehicles\n            .stream()\n            .map((vehicle) -> String.format("%s:%n%s", vehicle.getType(), vehicle)) // за да превърнем всеки елемент от списъка в String\n            .collect(Collectors.joining("")); // за да превърнем натрупаните стрингове в един общ\n}\nСподелете и вие, ако сте използвали Stream някъде :slight_smile:', '2021-12-20 01:00:00'),
	(11, 6, '4.01 - 5.01 календар на лекциите', 'Здравейте, @Java-Trainers,\nУтре и вдругиден по 50% курсисти имаме изпити от 10 до 16. Предполагам, че в тези дни няма да има лекции от 14:30 до 18:30? Питам, защото в момента имаме лекциите в календарите си.\n\nБлагодаря и поздрави,\nСаша', '2022-01-03 01:00:00'),
	(12, 2, 'DSA Tasks: Gotta catch ‘em all', 'Здравейте!\n\nВсяка изминала тема са ми по-малко процентите от Judge… Този път съм заседнал на 40/100.\n\nКакто винаги използвах spaghetti-at-the-wall и everything-but-the-kitchen-sink алгоритмите.\n\nИска ми се имахме повече примерен input за задачи като тази, или поне някаква индикация кое не му харесва на Judge. Това е решението 16 ми.\n\nhttps://pastebin.com/JrtjdRhp\n\nЧестно не се сещам как повече да го подобря. Някой със свежи идеи или жокери?', '2022-01-13 01:00:00'),
	(13, 34, 'Learn системата не работи', 'Здравейте. Мисля ,че вече от час нямам достъп то learn платформата. Няколко човека от бъди групата ми също имат същият проблем и бих искал да ви уведомя за него.', '2022-02-10 01:00:00'),
	(14, 32, 'Forum project - flag for deletion and cascade ops', 'Здравейте, ще получим ли някакво демо/инфо за това как да се справяме с триенето(флагването) на данни от таблицата, или трябва да си го ресърчнем сами?\n\nВтори въпрос -\nВлади доста пъти ни каза, да избягваме каскадни операции, но само при триенето, или като цяло, защото след рисърч тествахме, че това нещо работи ако искаме да имаме сет с коментари при модела пост? A какъв би бил друг възможен вариант, bi-directional връзка, която разгледахме днес?\n@OneToMany(mappedBy = “post”,\nfetch = FetchType.EAGER,\ncascade = {CascadeType.PERSIST, CascadeType.MERGE,\nCascadeType.DETACH, CascadeType.REFRESH})\nprivate Set comments;', '2022-02-23 01:00:00'),
	(15, 6, 'Importing project API to Swagger', 'Здравейте,\r\n\r\nНа консултацията зададох въпрос, как да импортирам нашето API в Swagger. Виктор @victor.valtchev спомена, че ще трябва да добавим dependency в самия проект и се разбрахме да отворя темичката за да сподели подробностите.\r\n\r\nБлагодаря и поздрави,\r\nСаша', '2022-03-04 01:00:00'),
	(16, 1, 'Unit Tests In Class Activity', 'Здравейте колеги,\n\nНякой успял ли да измисли как да се напишат тестовете в ProductRepositoryImpl за:\n\ngetCategories_should_returnCopyOfCollection()\n\ngetProducts_should_returnCopyOfCollection()\n\nНе мога да измисля как да тествам дали връща копие или не. (Знам че с == сравнявам адресите и ако е копие адресите няма да са едни и същи но не знам от къде да взема адреса на оригиналната колекция). Някой има ли идеи?', '2021-12-16 01:00:00'),
	(17, 1, 'Project Deployment - AWS, Heroku or anywhere else', 'Здравейте колеги,\r\n\r\nЗапочвам тази тема да обсъдим как можем да си deploy-нем проектите в някоя от многото възможни платформи. Ние засега сме пробвали без успех да deploy-нем в Heroku и използвайки AWS ElasticBeanstalk и срещнахме трудности и на двете места.\r\n\r\nАко някой е успял да deploy-не успешно ще е супер ако сподели.', '2022-03-21 01:00:00');
/*!40000 ALTER TABLE `posts` ENABLE KEYS */;

-- Dumping structure for table forum_database.posts_likes
CREATE TABLE IF NOT EXISTS `posts_likes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `post_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `likes_id_uindex` (`id`),
  KEY `likes_users_fk` (`user_id`),
  KEY `likes_posts_fk` (`post_id`),
  CONSTRAINT `likes_posts_fk` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
  CONSTRAINT `likes_users_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1603 DEFAULT CHARSET=latin1;

-- Dumping data for table forum_database.posts_likes: ~103 rows (approximately)
/*!40000 ALTER TABLE `posts_likes` DISABLE KEYS */;
REPLACE INTO `posts_likes` (`id`, `post_id`, `user_id`) VALUES
	(101, 1, 34),
	(102, 1, 4),
	(103, 1, 37),
	(201, 2, 37),
	(202, 2, 14),
	(203, 2, 22),
	(204, 2, 1),
	(205, 2, 15),
	(206, 2, 30),
	(207, 2, 34),
	(208, 2, 4),
	(301, 3, 3),
	(302, 3, 14),
	(303, 3, 1),
	(304, 3, 4),
	(305, 3, 2),
	(306, 3, 26),
	(307, 3, 32),
	(308, 3, 21),
	(401, 4, 1),
	(402, 4, 2),
	(403, 4, 32),
	(404, 4, 37),
	(405, 4, 19),
	(406, 4, 20),
	(407, 4, 15),
	(501, 5, 2),
	(502, 5, 14),
	(503, 5, 15),
	(504, 5, 4),
	(505, 5, 26),
	(506, 5, 1),
	(507, 5, 36),
	(508, 5, 5),
	(509, 5, 37),
	(510, 5, 19),
	(511, 5, 30),
	(512, 5, 18),
	(601, 6, 26),
	(602, 6, 32),
	(701, 7, 34),
	(702, 7, 20),
	(801, 8, 20),
	(802, 8, 33),
	(803, 8, 34),
	(804, 8, 15),
	(901, 9, 32),
	(1001, 10, 1),
	(1002, 10, 37),
	(1003, 10, 5),
	(1004, 10, 32),
	(1005, 10, 19),
	(1006, 10, 3),
	(1007, 10, 4),
	(1008, 10, 15),
	(1009, 10, 34),
	(1101, 11, 2),
	(1102, 11, 15),
	(1103, 11, 32),
	(1201, 12, 15),
	(1202, 12, 32),
	(1203, 12, 36),
	(1204, 12, 2),
	(1205, 12, 26),
	(1206, 12, 30),
	(1207, 12, 35),
	(1208, 12, 34),
	(1209, 12, 19),
	(1210, 12, 37),
	(1211, 12, 14),
	(1212, 12, 33),
	(1213, 12, 4),
	(1214, 12, 39),
	(1301, 13, 4),
	(1302, 13, 27),
	(1303, 13, 23),
	(1304, 13, 3),
	(1305, 13, 8),
	(1306, 13, 34),
	(1307, 13, 40),
	(1308, 13, 26),
	(1309, 13, 14),
	(1310, 13, 5),
	(1311, 13, 15),
	(1312, 13, 6),
	(1401, 14, 2),
	(1402, 14, 3),
	(1403, 14, 4),
	(1404, 14, 33),
	(1405, 14, 32),
	(1406, 14, 8),
	(1407, 14, 39),
	(1501, 15, 1),
	(1502, 15, 39),
	(1503, 15, 2),
	(1504, 15, 15),
	(1505, 15, 8),
	(1506, 15, 4),
	(1507, 15, 41),
	(1508, 15, 36),
	(1509, 15, 6),
	(1601, 16, 3),
	(1602, 16, 37);
/*!40000 ALTER TABLE `posts_likes` ENABLE KEYS */;

-- Dumping structure for table forum_database.posts_tags
CREATE TABLE IF NOT EXISTS `posts_tags` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `post_id` int(11) NOT NULL,
  `tag_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `posts_tags_table_posts_id_fk` (`post_id`),
  KEY `posts_tags_table_tag_types_id_fk` (`tag_id`),
  CONSTRAINT `posts_tags_table_posts_id_fk` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
  CONSTRAINT `posts_tags_table_tag_types_id_fk` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1708 DEFAULT CHARSET=latin1;

-- Dumping data for table forum_database.posts_tags: ~35 rows (approximately)
/*!40000 ALTER TABLE `posts_tags` DISABLE KEYS */;
REPLACE INTO `posts_tags` (`id`, `post_id`, `tag_id`) VALUES
	(101, 1, 1),
	(102, 1, 4),
	(201, 2, 2),
	(202, 2, 3),
	(203, 2, 5),
	(301, 3, 6),
	(302, 3, 7),
	(401, 4, 8),
	(402, 4, 9),
	(403, 4, 10),
	(501, 5, 11),
	(601, 6, 13),
	(602, 6, 14),
	(701, 7, 1),
	(702, 7, 11),
	(801, 8, 11),
	(802, 8, 13),
	(803, 8, 15),
	(901, 9, 12),
	(1001, 10, 15),
	(1002, 10, 16),
	(1101, 11, 17),
	(1201, 12, 18),
	(1202, 12, 19),
	(1203, 12, 20),
	(1301, 13, 1),
	(1302, 13, 17),
	(1401, 14, 21),
	(1402, 14, 22),
	(1403, 14, 23),
	(1501, 15, 24),
	(1601, 16, 11),
	(1602, 16, 13),
	(1706, 17, 18),
	(1707, 17, 25);
/*!40000 ALTER TABLE `posts_tags` ENABLE KEYS */;

-- Dumping structure for table forum_database.tags
CREATE TABLE IF NOT EXISTS `tags` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tag_types_id_uindex` (`id`),
  UNIQUE KEY `tag_types_name_uindex` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=latin1;

-- Dumping data for table forum_database.tags: ~26 rows (approximately)
/*!40000 ALTER TABLE `tags` DISABLE KEYS */;
REPLACE INTO `tags` (`id`, `name`) VALUES
	(17, 'administrative'),
	(5, 'buddy-groups'),
	(25, 'deploy'),
	(3, 'fun'),
	(15, 'generics'),
	(18, 'help'),
	(22, 'hibernate'),
	(12, 'homework'),
	(13, 'in-class-activity'),
	(8, 'interface'),
	(14, 'iteration'),
	(4, 'java'),
	(20, 'judge'),
	(7, 'logging'),
	(10, 'oop'),
	(23, 'orm'),
	(83, 'pleasehelpme'),
	(2, 'soft-skills'),
	(9, 'solid'),
	(21, 'spring'),
	(16, 'stream'),
	(24, 'swagger'),
	(19, 'tasks'),
	(1, 'technical'),
	(11, 'unit-test'),
	(6, 'workshop');
/*!40000 ALTER TABLE `tags` ENABLE KEYS */;

-- Dumping structure for table forum_database.tokens
CREATE TABLE IF NOT EXISTS `tokens` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `token` text NOT NULL,
  `createdAt` datetime NOT NULL,
  `expiredAt` datetime NOT NULL,
  `confirmedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tokens_users_id_fk` (`user_id`),
  CONSTRAINT `tokens_users_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- Dumping data for table forum_database.tokens: ~0 rows (approximately)
/*!40000 ALTER TABLE `tokens` DISABLE KEYS */;
/*!40000 ALTER TABLE `tokens` ENABLE KEYS */;

-- Dumping structure for table forum_database.users
CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(32) NOT NULL,
  `first_name` varchar(32) NOT NULL,
  `last_name` varchar(32) NOT NULL,
  `password` varchar(32) NOT NULL,
  `email` varchar(64) NOT NULL,
  `registration_date` datetime NOT NULL,
  `isAdmin` tinyint(1) NOT NULL DEFAULT 0,
  `isBlocked` tinyint(1) NOT NULL DEFAULT 0,
  `isDeleted` tinyint(1) NOT NULL DEFAULT 0,
  `isEnabled` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `users_user_id_uindex` (`id`),
  UNIQUE KEY `users_username_uindex` (`username`),
  UNIQUE KEY `users_email_uindex` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=latin1;

-- Dumping data for table forum_database.users: ~42 rows (approximately)
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
REPLACE INTO `users` (`id`, `username`, `first_name`, `last_name`, `password`, `email`, `registration_date`, `isAdmin`, `isBlocked`, `isDeleted`, `isEnabled`) VALUES
	(1, 'tihomir.dimitrov', 'TIhomir', 'Dimitrov', 'Alabalabanan!1', 'tihomir.dimitrov.95@gmail.com', '2022-02-08 12:23:20', 1, 0, 0, 1),
	(2, 'denislav.vuchkov', 'Denislav', 'Vuchkov', 'Alabalabanan!1', 'vy4kov@gmail.com', '2022-02-08 12:23:24', 1, 0, 0, 1),
	(3, 'p.pavlova', 'Plamenna', 'Pavlova', 'Alabalabanan!1', 'p.pavlova@yahoo.com', '2022-02-08 12:23:25', 1, 0, 0, 1),
	(4, 'chavdar.d', 'Chavdar', 'Dimitrov', 'Alabalabanan!1', 'chavdar.d@telerik.com', '2022-02-08 12:23:28', 0, 0, 0, 1),
	(5, 'dimitar.petrov', 'Dimitar', 'Petrov', 'Alabalabanan!1', 'd.petrov@telerik.com', '2022-02-08 12:23:29', 0, 0, 0, 1),
	(6, 'alex.velikova', 'Alexandra', 'Velikova', 'Alabalabanan!1', 'a.velikova@telerik.com', '2022-02-08 15:16:10', 0, 0, 0, 1),
	(7, 'georgi.krastev', 'Georgi', 'Krustev', 'Alabalabanan!1', 'g.krastev@telerik.com', '2022-02-08 15:16:40', 0, 0, 0, 1),
	(8, 'ivaylo.stavrev', 'Ivaylo', 'Stavrev', 'Alabalabanan!1', 'i.stavrev@@telerik.com', '2022-02-08 15:17:24', 0, 0, 0, 1),
	(9, 'd.seizov', 'Damian', 'Seizov', 'Alabalabanan!1', 'd.seizov@@telerik.com', '2022-02-08 15:18:01', 0, 0, 0, 1),
	(10, 's.hristov', 'Svetoslav', 'Hristov', 'Alabalabanan!1', 's.hristov@telerik.com', '2022-02-08 15:18:03', 0, 0, 0, 1),
	(11, 'andrey.mitev', 'Andrey', 'Mitev', 'Alabalabanan!1', 'a.mitev@telerik.com', '2022-02-08 15:19:24', 0, 0, 0, 1),
	(12, 'emi.kalinova', 'Emiliyana', 'Kalinova', 'Alabalabanan!1', 'e.kalinova@telerik.com', '2022-02-08 15:20:07', 0, 0, 0, 1),
	(13, 'z.georgiev', 'Zhelyazko', 'Georgiev', 'Alabalabanan!1', 'z.georgiev@gmail.com', '2022-02-08 15:20:37', 0, 0, 0, 1),
	(14, 'ivan.nikolov', 'Ivan', 'Nikolov', 'Alabalabanan!1', 'ivan.nikolov@abv.bg', '2022-02-08 15:21:02', 0, 0, 0, 1),
	(15, 'toshko.bonev', 'Todor', 'Bonev', 'Alabalabanan!1', 't.bonev@abv.bg', '2022-02-08 15:21:26', 0, 0, 0, 1),
	(16, 'ivan.chepilev', 'Ivan', 'Chepilev', 'Alabalabanan!1', 'i.chepilev@abv.bg', '2022-02-08 15:21:48', 0, 0, 0, 1),
	(17, 'georgi.dinkov', 'Georgi', 'Dinkov', 'Alabalabanan!1', 'g.dinkov@yahoo.com', '2022-02-08 15:21:55', 0, 0, 0, 1),
	(18, 'i.danchev', 'Iskren', 'Danchev', 'Alabalabanan!1', 'i.danchev@yahoo.com', '2022-02-08 15:22:41', 0, 0, 0, 1),
	(19, 'stef.sirakov', 'Stefan', 'Sirakov', 'Alabalabanan!1', 's.sirakov@gmail.com', '2022-02-08 15:23:15', 0, 0, 0, 1),
	(20, 'g.stukanyov', 'Georgi', 'Stukanyov', 'Alabalabanan!1', 'stukanyov@telerik.com', '2022-02-08 15:23:58', 0, 0, 1, 1),
	(21, 'ivan.malinov', 'Ivan', 'Malinov', 'Alabalabanan!1', 'i.malinov@telerik.com', '2022-02-08 15:24:15', 0, 0, 0, 1),
	(22, 'lilia.georgieva', 'Lilia', 'Georgieva', 'Alabalabanan!1', 'l.georgieva@telerik.com', '2022-02-08 15:24:18', 0, 1, 0, 1),
	(23, 'ivo.bankov', 'Ivo', 'Bankov', 'Alabalabanan!1', 'i.bankov@telerik.com', '2022-02-08 15:25:05', 0, 0, 0, 1),
	(24, 'ivan.petkov', 'Ivan', 'Petkov', 'Alabalabanan!1', 'ivan.petkov@telerik.com', '2022-02-08 15:25:34', 0, 0, 0, 1),
	(25, 'yordan.bradushev', 'Yordan', 'Bradushev', 'Alabalabanan!1', 'yordan.b@telerik.com', '2022-02-08 15:25:56', 0, 0, 0, 1),
	(26, 'niya.tsaneva', 'Niya', 'Tsaneva', 'Alabalabanan!1', 'niya.tsaneva@telerik.com', '2022-02-08 15:26:15', 0, 0, 0, 1),
	(27, 'martin.karalev', 'Martin', 'Karalev', 'Alabalabanan!1', 'martin.karalev@telerik.com', '2022-02-08 15:26:34', 0, 0, 0, 1),
	(28, 'ivaylo.ivanov', 'Ivaylo', 'Ivanov', 'Alabalabanan!1', 'ivaylo.ivanov@telerik.com', '2022-02-08 15:27:08', 0, 0, 0, 1),
	(29, 'plam.gospodinova', 'Plamena', 'Gospodinova', 'Alabalabanan!1', 'plam.gospodinova@telerik.com', '2022-02-08 15:27:25', 0, 0, 0, 1),
	(30, 'martina.dimova', 'Martina', 'Dimova', 'Alabalabanan!1', 'martina.dimova@telerik.com', '2022-02-08 15:27:46', 0, 0, 0, 1),
	(31, 'vilislav.angelov', 'Vilislav', 'Angelov', 'Alabalabanan!1', 'vilislav.angelov@telerik.com', '2022-02-08 15:28:08', 0, 0, 0, 1),
	(32, 'ivo.dimiev', 'Ivo', 'Dimiev', 'Alabalabanan!1', 'ivo.dimiev@telerik.com', '2022-02-08 15:28:25', 0, 0, 0, 1),
	(33, 'sofi.stoyanova', 'Sofi', 'Stoyanova', 'Alabalabanan!1', 'sofi.stoyanova@gmail.com', '2022-02-08 15:28:45', 0, 0, 0, 1),
	(34, 'angel.giurov', 'Angel', 'Guirov', 'Alabalabanan!1', 'angel.guirov@telerik.com', '2022-02-08 15:29:11', 0, 0, 0, 1),
	(35, 'martin.todorov', 'Martin', 'Todorov', 'Alabalabanan!1', 'martin.todorov@telerik.com', '2022-02-08 15:29:34', 0, 0, 0, 1),
	(36, 'yoanna.stoeva', 'Yoanna', 'Stoeva', 'Alabalabanan!1', 'yoanna.stoeva@telerik.com', '2022-02-08 15:29:50', 0, 0, 0, 1),
	(37, 'todor.andonov', 'Todor', 'Andonov', 'Alabalabanan!1', 'todor@learn.com', '2022-02-09 11:17:52', 0, 0, 0, 1),
	(38, 'boyan.hadzhiev', 'Boyan', 'Hadzhiev', 'Alabalabanan!1', 'boyand@learn.com', '2022-02-09 12:23:20', 0, 0, 0, 1),
	(39, 'victor.valtchev', 'Victor', 'Valtchev', 'Alabalabanan!1', 'victor@learn.com', '2022-03-21 14:31:12', 0, 0, 0, 1),
	(40, 'desislava.petrova', 'Desislava', 'Petrova', 'Alabalabanan!1', 'desi@learn.com', '2022-03-21 14:31:56', 0, 0, 0, 1),
	(41, 'vladimir.v', 'Vladimir', 'Venkov', 'Alabalabanan!1', 'vladi@learn.com', '2022-03-21 14:38:48', 0, 0, 0, 1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;

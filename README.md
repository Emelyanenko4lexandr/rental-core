# Rental Core

Универсальная библиотека базовых сущностей, репозиториев и сервисов для разработки платформ аренды на базе Spring Boot + JPA.

Библиотека предоставляет абстрактные классы, которые разработчик наследует и расширяет под свою предметную область — будь то аренда автомобилей, жилья, оборудования или любых других объектов.

## Требования

- Java 11+
- Spring Boot 2.7+

## Подключение зависимости

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/Emelyanenko4lexandr/rental-core</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>dev.rentalcore</groupId>
        <artifactId>rental-core</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

## Что включено

| Класс | Описание |
|---|---|
| `AbstractUser` | Базовый пользователь системы |
| `AbstractRentalProduct` | Базовый объект аренды |
| `AbstractRent` | Запись о сделке аренды |
| `AbstractMessage` | Системное сообщение |
| `AbstractUserService` | Сервис с базовой логикой работы с пользователями |
| `AbstractRentalProductService` | Сервис управления объектами аренды |
| `AbstractRentService` | Сервис управления арендой |
| `AbstractMessageService` | Сервис работы с сообщениями |

## Быстрый старт

**1. Создание сущностей:**

```java
@Entity
@Table(name = "users")
public class User extends AbstractUser {
    private String passportNumber;
    private String driverLicense;
}

@Entity
@Table(name = "automobiles")
public class Automobile extends AbstractRentalProduct<User> {
    private String brand;
    private String model;
    private String registrationNumber;
}
```

**2. Создание репозитория:**

```java
public interface UserRepository extends BaseUserRepository<User> {
    Optional<User> findByEmail(String email);
}

public interface AutomobileRepository 
        extends BaseRentalProductRepository<Automobile, User> {
    Optional<Automobile> findByRegistrationNumber(String number);
}
```

**3. Создание сервисов:**

```java
@Service
public class RentService extends AbstractRentService<Rent, Automobile, User> {

    public RentService(RentRepository rentRepo,
                       AutomobileRepository productRepo,
                       UserRepository userRepo) {
        super(rentRepo, productRepo, userRepo);
    }

    @Override
    protected Rent createRentInstance(User tenant, Automobile product) {
        return new Rent(tenant, product);
    }

    // Опционально — логика после начала аренды
    @Override
    protected void onRentStarted(Rent rent) {
        // например, списать средства с баланса
    }

    // Опционально — логика после завершения аренды
    @Override
    protected void onRentFinished(Rent rent) {
        // например, рассчитать итоговую стоимость
    }
}
```
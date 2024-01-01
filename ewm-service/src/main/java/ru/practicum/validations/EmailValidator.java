package ru.practicum.validations;

public class EmailValidator {
    /**
     * Проверяет локальную и доменную части email на длину.
     * @param email - email для проверки
     * @return true, если обе части удовлетворяют условиям; false иначе
     */
    public static boolean isEmailPartLengthValid(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        // Разделяем email по символу '@'
        int atIndex = email.indexOf('@');

        // Проверяем наличие '@' и его позицию
        if (atIndex <= 0 || atIndex == email.length() - 1) {
            return false; // неправильный формат email
        }

        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex + 1);

        // Проверка длины частей
        if (localPart.length() > 64) {
            return false; // локальная часть больше 64 символов
        }

        if (domainPart.length() > 63) {
            return false; // доменная часть больше 63 символов
        }

        // Опционально: можно добавить дополнительные проверки формата email

        return true;
    }
}

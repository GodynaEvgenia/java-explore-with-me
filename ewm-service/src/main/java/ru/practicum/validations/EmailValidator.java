package ru.practicum.validations;

public class EmailValidator {
    /**
     * Проверяет локальную и доменную части email на длину.
     *
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
        String domainPartWithPossibleSubdomains = email.substring(atIndex + 1);

        // Проверка длины локальной части
        if (localPart.length() > 64) {
            return false; // локальная часть больше 64 символов
        }

        // Получаем доменную часть до первой точки или всю строку, если точек нет
        int dotIndex = domainPartWithPossibleSubdomains.indexOf('.');

        String domainPart;
        if (dotIndex != -1) {
            domainPart = domainPartWithPossibleSubdomains.substring(0, dotIndex);
        } else {
            domainPart = domainPartWithPossibleSubdomains;
        }

        // Проверка длины доменной части (до первой точки)
        if (domainPart.length() > 63) {
            return false; // доменная часть больше 63 символов
        }

        // Можно добавить дополнительные проверки по формату

        return true;
    }
}

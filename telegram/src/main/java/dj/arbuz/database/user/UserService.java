package dj.arbuz.database.user;

import dj.arbuz.database.group.GroupDto;
import dj.arbuz.database.UserBase;
import dj.arbuz.user.BotUser;

import java.util.List;

public final class UserService implements UserBase {
    private final UserRepository userRepository = new UserRepository();

    /**
     * Метод добавляющий информацию о новых пользователях
     *
     * @param userId  id пользователя
     * @param botUser экземпляр класса пользователя
     * @return {@code true} - если пользователь был успешно добавлен,
     * {@code false} - если пользователь не был добавлен
     */
    @Override
    public boolean addUser(String userId, BotUser botUser) {
        UserDto dbSavedUser = userRepository.findByTelegramId(userId);

        if (dbSavedUser == null) {
            dbSavedUser = UserDto.builder()
                    .telegramId(botUser.getTelegramId())
                    .vkId(Long.valueOf(botUser.getId()))
                    .accessToken(botUser.getAccessToken())
                    .build();
            return userRepository.save(dbSavedUser) == dbSavedUser;
        }
        dbSavedUser.setVkId(Long.valueOf(botUser.getId()));
        dbSavedUser.setAccessToken(botUser.getAccessToken());
        return userRepository.update(dbSavedUser) == dbSavedUser;
    }

    /**
     * Метод проверяющий наличие пользователя в базе по id
     *
     * @param userId id пользователя
     * @return {@code true} - если пользователь с таким id есть в базе, {@code false} - если пользователя нет в базе
     */
    @Override
    public boolean contains(String userId) {
        return userRepository.findByTelegramId(userId) != null;
    }

    /**
     * Метод получающий пользователя по id
     *
     * @param userId id пользователя
     * @return экземпляр класса пользователя по id
     */
    @Override
    public BotUser getUser(String userId) {
        UserDto userDto = userRepository.findByTelegramId(userId);

        if (userDto == null) {
            return null;
        }

        return new BotUser(Math.toIntExact(userDto.getVkId()), userDto.getAccessToken(), userDto.getTelegramId());
    }

    @Override
    public List<String> getAllUsersId() {
        return userRepository.getAllUsersId();
    }

    @Override
    public boolean isAdmin(String userId) {
        UserDto userDto = userRepository.findByTelegramId(userId);

        if (userDto == null) {
            return false;
        }

        return userDto.getAdmin();
    }

    /**
     * Метод удаляющий пользователя из базы данных
     *
     * @param userTelegramId id пользователя в телеграме
     */
    @Override
    public void deleteUser(String userTelegramId) {
        UserDto botUser = userRepository.findByTelegramId(userTelegramId);

        if (botUser != null) {
            botUser.getSubscribedGroups().clear();
            userRepository.delete(botUser);
        }
    }

    public List<GroupDto> findUserSubscribedGroups(String userId) {
        UserDto userDto = userRepository.findByTelegramId(userId);

        if (userDto == null) {
            return List.of();
        }

        return List.copyOf(userDto.getSubscribedGroups());
    }
}

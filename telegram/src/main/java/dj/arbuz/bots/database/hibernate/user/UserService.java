package dj.arbuz.bots.database.hibernate.user;

import dj.arbuz.database.UserBase;
import dj.arbuz.bots.database.hibernate.group.GroupDto;
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
        UserDto userDto = UserDto.builder()
                .telegramId(botUser.getTelegramId())
                .vkId(Long.valueOf(botUser.getId()))
                .accessToken(botUser.getAccessToken())
                .build();

        if (contains(userId)) {
            return userRepository.update(userDto) == userDto;
        }

        return userRepository.save(userDto) == userDto;
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
        return new BotUser(Math.toIntExact(userDto.getVkId()), userDto.getAccessToken(), userDto.getTelegramId());
    }

    @Override
    public List<String> getAllUsersId() {
        return userRepository.getAllUsersId();
    }

    public List<GroupDto> findUserSubscribedGroups(String userId) {
        UserDto userDto = userRepository.findByTelegramId(userId);
        return userDto.getSubscribedGroups();
    }
}

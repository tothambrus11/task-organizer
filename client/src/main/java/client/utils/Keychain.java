package client.utils;

import client.contexts.UserContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import commons.models.BoardInfo;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Keychain is a utility class to manage, store, and retrieve entries
 * containing join keys, passwords, and other related data of the boards
 * you have previously joined.
 */
public class Keychain {
    private static final String STORAGE_FILE = "keychain.json";

    private Map<String, Map<String, Map<String, BoardInfo>>> entries;
    private ObjectMapper objectMapper;

    private ServerUtils serverUtils;
    private UserContext userContext;

    /**
     * Constructs a new Keychain and initializes it by loading
     * stored entries from the keychain.json file.
     */
    public Keychain(ServerUtils serverUtils, UserContext userContext) {
        this.serverUtils = serverUtils;
        this.userContext = userContext;

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        loadFromFile();
    }

    private Map<String, BoardInfo> getCurrentEntries() {
        if (userContext.getIsAdmin()) return null;

        var server = serverUtils.getServerAddress();
        var user = userContext.getUsername();

        if (!entries.containsKey(server)) entries.put(server, new HashMap<>());
        var serverEntries = entries.get(server);

        if (!serverEntries.containsKey(user)) serverEntries.put(user, new HashMap<>());
        var userEntries = serverEntries.get(user);

        return userEntries;
    }

    /**
     * Retrieves an entry by its join key.
     *
     * @param joinKey the join key associated with the entry
     * @return the BoardInfo with the specified join key or null if not found
     */
    public BoardInfo getEntry(String joinKey) {
        return getCurrentEntries().get(joinKey);
    }

    /**
     * Retrieves a list of entries sorted by recency of last join time.
     *
     * @return a list of BoardInfo objects sorted by descending last join time
     */
    public List<BoardInfo> getEntriesByRecency() {
        return getCurrentEntries().values().stream()
                .sorted(Comparator.nullsLast(Comparator.comparing(BoardInfo::getLastJoinTime,
                        Comparator.nullsLast(Comparator.naturalOrder())))).collect(Collectors.toList());
    }

    /**
     * Adds a new entry to the keychain with the specified join key, title, and creator.
     * This method also saves the updated keychain to the storage file.
     *
     * @param info new updated BoardInfo object
     */
    public void updateEntry(BoardInfo info) {
        if (userContext.getIsAdmin()) return;

        info.setLastJoinTime(LocalDateTime.now());

        var entries = getCurrentEntries();
        if (entries.containsKey(info.getJoinKey())) {
            entries.get(info.getJoinKey()).update(info);
        } else {
            entries.put(info.getJoinKey(), info);
        }

        saveToFile();
    }

    public void refresh(Runnable callback) {
        loadFromFile();

        var server = serverUtils.getServerAddress();
        var user = userContext.getUsername();

        userContext.requestRefresh(boards -> {
            if (Objects.equals(server, serverUtils.getServerAddress()) &&
                Objects.equals(user, userContext.getUsername())) {

                var keysToRefresh = getCurrentEntries().values().stream().map(BoardInfo::getJoinKey).toList();

                for (BoardInfo board : boards)
                    if (keysToRefresh.contains(board.getJoinKey())) {
                        updateEntry(board);
                        System.out.println("Updated entry: " + board);
                    }

                // delete entries that no longer exist
                var remainingKeys = boards.stream().map(BoardInfo::getJoinKey).collect(Collectors.toList());
                var deleteKeys = keysToRefresh.stream().filter(key -> !remainingKeys.contains(key));
                deleteKeys.forEach(this::removeEntry);

                System.out.println("Refreshed keychain");
            } else {
                System.out.println("Keychain refresh skipped");
            }

            if (callback != null)
                callback.run();
        });

        saveToFile();
    }

    /**
     * Removes an entry from the keychain using its join key.
     * This method also saves the updated keychain to the storage file.
     *
     * @param joinKey the join key associated with the entry to be removed
     */
    public void removeEntry(String joinKey) {
        if (userContext.getIsAdmin()) return;

        getCurrentEntries().remove(joinKey);
        saveToFile();
    }

    /**
     * Loads keychain data from the storage file.
     * If the storage file does not exist, an empty keychain is created.
     */
    void loadFromFile() {
        File file = new File(STORAGE_FILE);

        if (file.exists()) {
            try {
                entries = objectMapper.readValue(file, new TypeReference<>() {
                });
            } catch (IOException e) {
                System.out.println("Failed to load keychain data from file.");
                entries = new HashMap<>();
                //throw new RuntimeException("Failed to load keychain data from file.", e);
            }
        } else {
            entries = new HashMap<>();
        }
    }

    /**
     * Saves the keychain data to the storage file.
     * If an error occurs during the save process, a RuntimeException is thrown.
     */
    void saveToFile() {
        try {
            objectMapper.writeValue(new File(STORAGE_FILE), entries);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save keychain data to file.", e);
        }
    }
}
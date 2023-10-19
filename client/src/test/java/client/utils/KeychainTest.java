package client.utils;

import client.contexts.UserContext;
import commons.models.BoardInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class KeychainTest {

    @Mock
    private ServerUtils serverUtils;

    @Mock
    private UserContext userContext;

    @Spy
    @InjectMocks
    private Keychain keychain;

    private BoardInfo testBoardInfo;

    @BeforeEach
    public void setUp() {
        testBoardInfo = new BoardInfo();
        testBoardInfo.setId(UUID.randomUUID());
        testBoardInfo.setJoinKey("test-join-key");
        testBoardInfo.setTitle("Test Board");
        testBoardInfo.setCreator("user1");
        testBoardInfo.setLastJoinTime(LocalDateTime.now());

        // Mock loadFromFile() and saveToFile() to avoid filesystem interactions
        lenient().doNothing().when(keychain).loadFromFile();
        lenient().doNothing().when(keychain).saveToFile();
    }

    @Test
    public void testUpdateEntry() {
        keychain.updateEntry(testBoardInfo);
        BoardInfo result = keychain.getEntry(testBoardInfo.getJoinKey());
        assertEquals(testBoardInfo, result);
    }

    @Test
    public void testRemoveEntry() {
        keychain.updateEntry(testBoardInfo);
        keychain.removeEntry(testBoardInfo.getJoinKey());
        BoardInfo result = keychain.getEntry(testBoardInfo.getJoinKey());
        assertNull(result);
    }

    @Test
    public void testGetEntry() {
        keychain.updateEntry(testBoardInfo);
        BoardInfo result = keychain.getEntry(testBoardInfo.getJoinKey());
        assertEquals(testBoardInfo, result);
    }

    @Test
    public void testGetEntriesByRecency() {
        BoardInfo anotherBoardInfo = new BoardInfo();
        anotherBoardInfo.setId(UUID.randomUUID());
        anotherBoardInfo.setJoinKey("another-join-key");
        anotherBoardInfo.setTitle("Another Board");
        anotherBoardInfo.setCreator("user2");
        anotherBoardInfo.setLastJoinTime(LocalDateTime.now().minusHours(1));

        keychain.updateEntry(testBoardInfo);
        keychain.updateEntry(anotherBoardInfo);

        List<BoardInfo> result = keychain.getEntriesByRecency();
        assertEquals(2, result.size());
        assertEquals(testBoardInfo, result.get(0));
        assertEquals(anotherBoardInfo, result.get(1));
    }
}

package me.stahu.gsblockshuffle;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class GSBlockShuffleTests {

    @Test
    void onEnable_doesNotThrow() {
        // Mock the JavaPlugin environment
        GSBlockShuffle gsBlockShuffle = Mockito.mock(GSBlockShuffle.class);
        Mockito.doCallRealMethod().when(gsBlockShuffle).onEnable();

        // Ensure that onEnable doesn't throw any exceptions
        assertDoesNotThrow(gsBlockShuffle::onEnable);
    }
}

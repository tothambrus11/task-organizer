package client.utils;

import javafx.application.Platform;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class FXTest {
    // counts down to zero when initialized
    static CountDownLatch initialized = new CountDownLatch(1);
    static AtomicBoolean startedInitializing = new AtomicBoolean(false);

    @BeforeAll
    static void initJfxRuntime() throws InterruptedException {
        if (startedInitializing.getAndSet(true)) {
            System.out.println("[FXTest] Waiting for JavaFX toolkit initialization latch...");
            initialized.await(); // wait until initialized
            return;
        }

        System.out.println("[FXTest] Initializing JavaFX toolkit...");
        Platform.startup(() -> {
            System.out.println("[FXTest] JavaFX toolkit initialized.");
            initialized.countDown();
        });

        initialized.await();
    }

    @AfterAll
    static void stopPlatform(){
        Platform.exit();
    }
}

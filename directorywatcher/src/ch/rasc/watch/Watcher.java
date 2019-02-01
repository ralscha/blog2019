package ch.rasc.watch;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class Watcher {
	public static void main(String[] args) throws IOException, InterruptedException {
		try (WatchService watchService = FileSystems.getDefault().newWatchService()) {

			Path path = Paths.get("e:/watchme");

			path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.ENTRY_DELETE,
					StandardWatchEventKinds.OVERFLOW);

			while (true) {
				WatchKey key = watchService.take();

				for (WatchEvent<?> watchEvent : key.pollEvents()) {

					Kind<?> kind = watchEvent.kind();
					Path context = (Path) watchEvent.context();

					System.out.printf("%s:%s\n", context, kind);
				}

				boolean valid = key.reset();
				if (!valid) {
					break;
				}
			}
		}
	}

}

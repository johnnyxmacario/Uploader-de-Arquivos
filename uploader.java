import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class uploader {import org.springfram
    ework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

    @RestController
    @RequestMapping("/files")
    public class FileController {
        private static final String UPLOAD_DIR = "uploads/";

        private final S3Client s3Client;

        @Value("${aws.s3.bucket}")
        private String bucketName;

        @Autowired
        private JdbcTemplate jdbcTemplate;

        public FileController(S3Client s3Client) {
            this.s3Client = s3Client;
        }

        @PostMapping("/upload")
        public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
            try {
                if (file.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Arquivo vazio!");
                }

                File dir = new File(UPLOAD_DIR);
                if (!dir.exists()) dir.mkdirs();

                Path filePath = Paths.get(UPLOAD_DIR + file.getOriginalFilename());
                Files.write(filePath, file.getBytes());

                // Upload para o S3
                s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(file.getOriginalFilename())
                        .build(), filePath);

                // Salvar no banco de dados
                jdbcTemplate.update("INSERT INTO files (filename, path) VALUES (?, ?)",
                        file.getOriginalFilename(), filePath.toString());

                return ResponseEntity.ok("Arquivo enviado com sucesso: " + file.getOriginalFilename());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao enviar o arquivo!");
            }
        }

        @GetMapping("/download/{filename}")
        public ResponseEntity<byte[]> downloadFile(@PathVariable String filename) {
            try {
                Path filePath = Paths.get(UPLOAD_DIR + filename);
                byte[] fileBytes = Files.readAllBytes(filePath);
                return ResponseEntity.ok().body(fileBytes);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        }

        // SQL Script para criar a tabela
        public static final String CREATE_TABLE_SQL = """
        CREATE TABLE files (
           id SERIAL PRIMARY KEY,
            filename VARCHAR(255) NOT NULL,
            path TEXT NOT NULL,
            uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
    """;
    }

}

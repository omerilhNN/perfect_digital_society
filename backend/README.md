# Perfect Digital Society

Bu proje, mükemmel dijital toplum konseptini gerçekleştirmek için geliştirilmiş bir Spring Boot uygulamasıdır.

## Özellikler

- Kullanıcı yönetimi ve kimlik doğrulama
- Topluluk kuralları yönetimi
- Mesajlaşma sistemi
- Bakiye yönetimi
- Admin paneli
- JWT tabanlı güvenlik
- Swagger API dokümantasyonu

## Teknolojiler

- Java 17
- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- H2 Database (geliştirme için)
- MapStruct
- Swagger/OpenAPI
- Maven

## Kurulum

1. Java 17'nin yüklü olduğundan emin olun
2. Projeyi klonlayın:
   ```bash
   git clone https://github.com/[username]/Perfect-Digital-Society.git
   cd Perfect-Digital-Society
   ```

3. Maven ile projeyi derleyin:
   ```bash
   ./mvnw clean install
   ```

4. Uygulamayı çalıştırın:
   ```bash
   ./mvnw spring-boot:run
   ```

## API Dokümantasyonu

Uygulama çalıştıktan sonra aşağıdaki URL'lerden API dokümantasyonuna erişebilirsiniz:

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Endpoint'ler

Detaylı API dokümantasyonu için `API_DOCUMENTATION_COMPLETE.md` ve `POSTMAN_API_DOCUMENTATION.md` dosyalarına bakınız.

## Test

```bash
./mvnw test
```

## Postman Collection

Proje kök dizininde bulunan Postman collection ve environment dosyalarını Postman'e import edebilirsiniz:

- `Perfect_Digital_Society_API.postman_collection.json`
- `Perfect_Digital_Society_Environment.postman_environment.json`

## Katkıda Bulunma

1. Fork edin
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Commit edin (`git commit -m 'Add amazing feature'`)
4. Branch'i push edin (`git push origin feature/amazing-feature`)
5. Pull Request açın

## Lisans

Bu proje MIT lisansı altında lisanslanmıştır.

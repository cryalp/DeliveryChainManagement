# SSL Cert Creation
keytool -genkey -alias springboot -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.pfx -validity 3650
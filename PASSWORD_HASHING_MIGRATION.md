# Password Hashing Implementation - Migration Guide

## Overview
This project has been updated to use **BCrypt password hashing** via Spring Security instead of storing passwords in plaintext. This significantly improves security by making it impossible to retrieve original passwords from the database.

## Changes Made

### 1. **Dependencies Added (pom.xml)**
- `spring-security-crypto:6.2.1` - Provides BCrypt password hashing
- `commons-logging:1.2` - Required dependency for Spring Security

### 2. **New Security Utility Class**
- **File**: `src/main/java/com/fitness/util/SecurityUtil.java`
- **Methods**:
  - `hashPassword(String plainPassword)` - Hashes a plaintext password using BCrypt
  - `checkPassword(String plainPassword, String hashedPassword)` - Verifies a plaintext password against a hashed password

### 3. **Updated Code Files**

#### UserDAO.java
- **createUser()**: Now hashes passwords before storing them in the database
- **getUserByEmail()**: Retrieves from `hashed_password` column instead of `password`

#### LoginServlet.java  
- Uses `SecurityUtil.checkPassword()` to verify passwords instead of plain text comparison
- Removed debug logging that printed passwords

#### RegisterServlet.java
- No changes needed - still receives plain text password from form, but UserDAO handles hashing

### 4. **Database Schema Changes**

#### Updated Column Names
- `password` → `hashed_password` (VARCHAR(255))

#### Updated Files
- `macromind_complete_database_setup.sql` - Main schema file
- Created `migrate_to_hashed_passwords.sql` - Migration script for existing databases
- Created `fix_users_table.sql` - Additional migration helper

## Migration Steps

### For New Installations
If setting up the database from scratch, simply run:
```sql
source macromind_complete_database_setup.sql
```

The schema already includes the `hashed_password` column.

### For Existing Databases

**⚠️ WARNING**: This migration will invalidate all existing passwords. All users will need to re-register or reset their passwords.

1. **Backup your database first!**
   ```sql
   mysqldump -u root -p your_database > backup.sql
   ```

2. **Run the migration script:**
   ```sql
   source migrate_to_hashed_passwords.sql
   ```

   This will:
   - Rename `password` column to `hashed_password`
   - Clear all existing password values (for security)
   - Create a backup table `users_backup`

3. **Alternative: Keep test data** (Development only)
   
   If you want to keep users with a known password for testing:
   - Uncomment the line in `migrate_to_hashed_passwords.sql` that sets a default hash
   - Default test password will be: `password123`

## How It Works

### Registration Flow
1. User enters plaintext password in registration form
2. `RegisterServlet` receives plaintext password
3. `UserDAO.createUser()` calls `SecurityUtil.hashPassword()` to hash it
4. Hashed password (starting with `$2a$`) is stored in database

### Login Flow
1. User enters plaintext password in login form  
2. `LoginServlet` receives plaintext password
3. `UserDAO.getUserByEmail()` retrieves hashed password from database
4. `SecurityUtil.checkPassword()` verifies plaintext against hash
5. Login succeeds if passwords match

### BCrypt Hash Example
```
Plain text: mySecretPassword123
BCrypt hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
```

## Security Benefits

1. **One-way encryption**: Impossible to reverse BCrypt hashes to get original passwords
2. **Salt included**: Each password has a unique salt, preventing rainbow table attacks
3. **Adaptive cost**: BCrypt's work factor can be increased as computers get faster
4. **Industry standard**: Used by major organizations worldwide

## Testing

All tests have been updated to work with password hashing:
- `UserDAOTest` now verifies BCrypt hashes are stored
- Tests use `ArgumentCaptor` to verify hash format (`$2a$` prefix)

Run tests:
```bash
mvn test
```

## Deployment Checklist

- [ ] Backup production database
- [ ] Run migration script on staging environment first
- [ ] Test login/registration on staging
- [ ] Notify users about password reset requirement
- [ ] Deploy updated WAR file
- [ ] Run migration script on production
- [ ] Monitor application logs for any issues

## Troubleshooting

### Issue: "Invalid email or password" for all users
**Solution**: Database migration hasn't been run. Check if `hashed_password` column exists and contains BCrypt hashes (starting with `$2a$`).

### Issue: New users can't register
**Solution**: Verify Spring Security dependency was downloaded. Run `mvn clean compile` to ensure dependencies are resolved.

### Issue: ClassNotFoundException for BCryptPasswordEncoder
**Solution**: Make sure `spring-security-crypto` and `commons-logging` dependencies are in your WAR file's `WEB-INF/lib` directory.

## Next Steps (Optional Enhancements)

Consider implementing:
1. Password strength requirements (minimum length, special characters, etc.)
2. Password reset functionality via email
3. Account lockout after failed login attempts
4. Two-factor authentication (2FA)
5. Password history to prevent reusing old passwords

## Support

For issues or questions, check:
- Maven dependencies are properly downloaded: `~/.m2/repository/org/springframework/security/`
- Database column renamed: `DESCRIBE users;` should show `hashed_password`
- Application logs for specific error messages

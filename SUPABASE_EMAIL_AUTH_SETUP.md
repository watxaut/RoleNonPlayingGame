# Supabase Email Authentication Setup Guide

This guide explains how to configure your Supabase project to enable email-based authentication for
the Role Non-Playing Game Android app.

## Overview

The app now uses email-based authentication instead of anonymous authentication. Users must sign up
with an email and password before they can create heroes (characters).

## Supabase Dashboard Configuration

Follow these steps to enable email authentication in your Supabase dashboard:

### 1. Enable Email Provider

1. Go to your Supabase project dashboard at https://app.supabase.com
2. Navigate to **Authentication** → **Providers** in the left sidebar
3. Find the **Email** provider in the list
4. Make sure the **Email** provider is **enabled** (it should be enabled by default)

### 2. Configure Email Settings

1. In the **Authentication** → **Providers** → **Email** section, configure the following settings:

   **Confirm email:**
    - **Recommended for production**: Enable this option
    - **For development/testing**: Disable this option to skip email confirmation
    - When enabled, users will receive a confirmation email before they can sign in
    - When disabled, users can sign in immediately after registration

   **Secure email change:**
    - Enable this if you want to require email confirmation when users change their email address

2. Click **Save** to apply the changes

### 3. Configure Email Templates (Optional but Recommended)

1. Navigate to **Authentication** → **Email Templates**
2. Customize the email templates for:
    - **Confirm signup** - Sent when users register (if email confirmation is enabled)
    - **Invite user** - Sent when you invite users
    - **Magic Link** - Sent for passwordless login (not used in this app)
    - **Change Email Address** - Sent when users change their email
    - **Reset Password** - Sent when users request a password reset

3. You can customize the:
    - Subject line
    - Email body (supports HTML)
    - Sender name

### 4. Configure Auth Settings

1. Navigate to **Authentication** → **Settings**
2. Configure the following options:

   **Site URL:**
    - Set this to your app's URL scheme or website URL
    - For Android: `com.watxaut.rolenonplayinggame://`
    - This is used for email redirects

   **Redirect URLs:**
    - Add any additional URLs that are allowed for redirects after email confirmation
    - For Android, you may need to add: `com.watxaut.rolenonplayinggame://auth-callback`

   **JWT Settings:**
    - **JWT expiry limit**: Default is 3600 seconds (1 hour)
    - Adjust based on your security requirements

   **Security Settings:**
    - **Enable phone signup**: Leave disabled (not used in this app)
    - **Enable phone confirmations**: Leave disabled (not used in this app)
    - **Disable email signups**: Leave disabled (we want email signups)
    - **Enable manual linking**: Optional

   **Password Settings:**
    - **Minimum password length**: Default is 6 characters (matches our app validation)
    - Adjust if you want stricter password requirements

3. Click **Save** to apply the changes

### 5. Disable Anonymous Authentication (Important!)

Since we're moving from anonymous to email-based authentication:

1. Navigate to **Authentication** → **Providers**
2. Find **Anonymous** provider
3. **Disable** the anonymous provider
4. Click **Save**

**Note:** If you have existing anonymous users, you may want to keep it enabled temporarily and
handle migration. However, for new deployments, disable it immediately.

### 6. Update Row Level Security (RLS) Policies

Your existing RLS policies should work with email authentication, but verify:

1. Navigate to **Authentication** → **Policies**
2. Check the policies for the `characters` table
3. Ensure policies use `auth.uid()` to identify users (this works for both anonymous and
   email-authenticated users)

Example policy (should already be in place from `supabase/enable_rls_with_anonymous_auth.sql`):

```sql
-- Users can read their own characters
CREATE POLICY "Users can read own characters"
ON characters FOR SELECT
USING (auth.uid() = user_id);

-- Users can insert their own characters
CREATE POLICY "Users can insert own characters"
ON characters FOR INSERT
WITH CHECK (auth.uid() = user_id);

-- Users can update their own characters
CREATE POLICY "Users can update own characters"
ON characters FOR UPDATE
USING (auth.uid() = user_id)
WITH CHECK (auth.uid() = user_id);

-- Users can delete their own characters
CREATE POLICY "Users can delete own characters"
ON characters FOR DELETE
USING (auth.uid() = user_id);
```

### 7. Test Email Delivery (For Production)

1. Navigate to **Authentication** → **Settings** → **SMTP Settings**
2. By default, Supabase uses their email service (limited to 3-4 emails per hour for free tier)
3. For production, configure your own SMTP server:
    - Enable **Custom SMTP**
    - Enter your SMTP host, port, username, and password
    - Test the connection

**Recommended SMTP providers:**

- SendGrid
- Amazon SES
- Mailgun
- Postmark

### 8. Monitor Authentication Events

1. Navigate to **Authentication** → **Users**
2. You can view all registered users
3. Manually verify or delete users if needed
4. View user metadata and sessions

### 9. Rate Limiting (Security)

1. Navigate to **Authentication** → **Rate Limits**
2. Configure rate limits to prevent abuse:
    - **Email signups**: Recommended 3-5 per hour per IP
    - **Email signins**: Recommended 30 per hour per IP
    - **Password recovery**: Recommended 3 per hour per IP

## Testing the Setup

### For Development (Email Confirmation Disabled)

1. Build and run the app
2. You should see the Login screen on first launch
3. Click "Don't have an account? Sign Up"
4. Enter an email and password (minimum 6 characters)
5. Click "Sign Up"
6. You should be immediately signed in and redirected to the Home screen
7. Try creating a hero - it should work without errors

### For Production (Email Confirmation Enabled)

1. Sign up with a real email address
2. Check your email for the confirmation link
3. Click the confirmation link
4. Return to the app and sign in with your credentials
5. Try creating a hero

## Troubleshooting

### "Invalid credentials" error when signing in

- Verify the email and password are correct
- If email confirmation is enabled, make sure the user has confirmed their email
- Check the Supabase dashboard → Authentication → Users to see if the user exists

### Users not receiving confirmation emails

- Check the Supabase dashboard → Authentication → Settings → SMTP Settings
- Verify your SMTP configuration is correct
- Check spam/junk folders
- For free tier, check if you've exceeded the email limit (3-4 per hour)

### RLS policy errors

- Verify RLS policies are correctly set up for the `characters` table
- Check that policies use `auth.uid()` not `user_id()`
- Test policies in the Supabase SQL editor

### App crashes on character creation

- Check Logcat for authentication errors
- Verify the user is authenticated before creating a character
- Ensure the Supabase URL and API key in `local.properties` are correct

## Migration from Anonymous Authentication

If you have existing anonymous users:

1. **Option 1: Keep both** - Allow both anonymous and email authentication temporarily
2. **Option 2: Migrate data** - Write a migration script to link anonymous characters to email
   accounts
3. **Option 3: Fresh start** - Disable anonymous, delete anonymous users, start fresh

For most new projects, **Option 3** is recommended.

## Security Best Practices

1. **Always enable email confirmation in production**
2. **Use a custom SMTP provider for production** (not Supabase's default)
3. **Set up rate limiting** to prevent abuse
4. **Use strong password requirements** (consider increasing minimum length to 8-12 characters)
5. **Enable two-factor authentication** in the future if needed
6. **Regularly review user accounts** for suspicious activity
7. **Set up proper password reset flows** with email verification

## Next Steps

After configuring Supabase:

1. Update `local.properties` with your Supabase URL and API key (if not already done)
2. Build and test the app
3. Consider adding password reset functionality
4. Consider adding email change functionality
5. Consider adding profile picture support
6. Set up production email templates with your branding

## Support

For more information, refer to:

- [Supabase Auth Documentation](https://supabase.com/docs/guides/auth)
- [Supabase Email Auth Guide](https://supabase.com/docs/guides/auth/auth-email)
- [Supabase Android SDK](https://supabase.com/docs/reference/kotlin/introduction)

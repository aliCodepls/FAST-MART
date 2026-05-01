# Google Drive Sharing Guide for FastMart

To ensure that images load correctly in the application, your Google Drive files must be shared publicly. If a file is private, the app will receive a `403 Forbidden` error and show a placeholder instead.

## 1. Set Permissions to "Public"
Follow these steps for every image you use in the database:
1. Open [Google Drive](https://drive.google.com).
2. **Right-click** on the image file.
3. Select **Share** > **Share**.
4. Under "General access", click the dropdown (usually says "Restricted").
5. Select **"Anyone with the link"**.
6. Ensure the role is set to **"Viewer"**.
7. Click **Done**.

## 2. Verify the Setting
You can verify if the file is correctly shared by:
1. Copying the link.
2. Opening an **Incognito/Private window** in your browser.
3. Pasting the link.
4. If you can see the image or the file preview without logging in, the app will be able to load it.

## 3. Bulk Sharing (Optional)
If you have many images, it is recommended to:
1. Put all images in a **single folder**.
2. Share the **folder** as "Anyone with the link can view".
3. Any file you move into this folder will automatically inherit the public permission.

---
**Note**: The app now has a built-in retry mechanism that tries 3 different ways to load your Drive links, but they all depend on this "Anyone with the link" setting.

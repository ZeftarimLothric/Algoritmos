import tkinter as tk
from tkinter import ttk, messagebox
import instaloader
from PIL import Image, ImageTk
import requests
from io import BytesIO

# Función que obtiene la información de Instagram
def obtener_info():
    username = entry_usuario.get()

    if username == "":
        messagebox.showwarning("Advertencia", "Por favor ingresa un nombre de usuario.")
        return

    L = instaloader.Instaloader()

    try:
        # Obtener perfil de Instagram sin el "@"
        if username.startswith('@'):
            username = username[1:]

        # Obtener perfil de Instagram
        profile = instaloader.Profile.from_username(L.context, username)

        # Mostrar información básica
        info_text.config(state=tk.NORMAL)
        info_text.delete(1.0, tk.END)  # Limpiar el área de texto
        info_text.insert(tk.END, f"Nombre: {profile.full_name}\n")
        info_text.insert(tk.END, f"Bio: {profile.biography}\n")
        info_text.insert(tk.END, f"Seguidores: {profile.followers}\n")
        info_text.insert(tk.END, f"Siguiendo: {profile.followees}\n")
        info_text.insert(tk.END, f"Publicaciones: {profile.mediacount}\n")

        # Verificar si el perfil es privado
        if profile.is_private:
            info_text.insert(tk.END, "\nEste perfil es privado.")
        else:
            # Obtener la imagen de perfil
            img_url = profile.profile_pic_url
            img_response = requests.get(img_url)
            img_data = img_response.content
            img = Image.open(BytesIO(img_data))
            img.thumbnail((150, 150))  # Cambiar el tamaño de la imagen
            img = ImageTk.PhotoImage(img)
            
            # Mostrar la imagen en la interfaz
            image_label.config(image=img)
            image_label.image = img  # Guardar la referencia de la imagen
            info_text.insert(tk.END, "\nImagen de perfil: ")
        info_text.config(state=tk.DISABLED)
    except instaloader.exceptions.ProfileNotExistsException:
        messagebox.showerror("Error", "El perfil no existe.")
    except Exception as e:
        messagebox.showerror("Error", str(e))

# Crear la ventana principal
root = tk.Tk()
root.title("Información de Instagram")
root.geometry("450x500")
root.resizable(False, False)

# Estilo de ttk
style = ttk.Style()
style.configure("TLabel", font=("Helvetica", 12))
style.configure("TButton", font=("Helvetica", 12))
style.configure("TEntry", font=("Helvetica", 12))

# Frame principal
main_frame = ttk.Frame(root, padding="10")
main_frame.pack(fill=tk.BOTH, expand=True)

# Etiqueta de usuario
label_usuario = ttk.Label(main_frame, text="Ingresa el nombre de usuario de Instagram (sin '@'):")
label_usuario.pack(pady=10)

# Entrada para el nombre de usuario
entry_usuario = ttk.Entry(main_frame, width=30)
entry_usuario.pack(pady=10)

# Botón para obtener la información
boton_obtener = ttk.Button(main_frame, text="Obtener Información", command=obtener_info)
boton_obtener.pack(pady=10)

# Área de texto para mostrar la información
info_text = tk.Text(main_frame, height=8, width=40, state=tk.DISABLED, wrap=tk.WORD)
info_text.pack(pady=10)

# Etiqueta para la imagen de perfil
image_label = ttk.Label(main_frame)
image_label.pack(pady=10)

# Ejecutar la aplicación
root.mainloop()
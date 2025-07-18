import { clsx, type ClassValue } from "clsx"
import { twMerge } from "tailwind-merge"

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

/**
 * Giải mã một chuỗi Base64 URL-encoded (giống Java UrlEncryptor.encodeUrl)
 * và trả về chuỗi đã giải mã.
 */
export function urlDecrypt(encoded: string): string {
  // Replace URL-safe chars and pad if needed
  let base64 = encoded.replace(/-/g, "+").replace(/_/g, "/");
  while (base64.length % 4 !== 0) {
    base64 += "=";
  }
  try {
    return decodeURIComponent(
      Array.prototype.map.call(atob(base64), (c: string) => {
        return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
      }).join("")
    );
  } catch (e) {
    return atob(base64);
  }
}

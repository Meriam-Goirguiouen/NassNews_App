export function getPosition() {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject("Géolocalisation non supportée");
    } else {
      navigator.geolocation.getCurrentPosition(resolve, reject);
    }
  });
}

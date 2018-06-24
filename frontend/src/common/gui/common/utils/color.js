export function getColor(primary = false, secondary = false) {
  return primary ? "primary" : secondary ? "secondary" : "default";
}

export default function statusMessage(error) {
  return error.response == null ? null : error.response.data;
}

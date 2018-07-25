export default function statusCode(error) {
  return error.response == null ? -1 : error.response.status;
}

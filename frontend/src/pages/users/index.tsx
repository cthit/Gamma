import { useUsersLoaderData } from "./loader";
import { Link } from "react-router-dom";

const tableCellStyle = "border border-slate-300 p-1";

export const UsersPage = () => {
  const { users } = useUsersLoaderData();

  return (
    <table
      className={
        "m-auto w-full table-fixed border-collapse border border-slate-400"
      }
    >
      <thead>
        <tr>
          <th className={tableCellStyle + " text-left"}>First name</th>
          <th className={tableCellStyle + " text-left"}>Nick</th>
          <th className={tableCellStyle + " text-left"}>Last name</th>
          <th className={tableCellStyle + " text-left"}>Acceptance year</th>
          <th>Details</th>
        </tr>
      </thead>
      <tbody>
        {users.map((user) => (
          <tr key={user.id} className={tableCellStyle}>
            <td className={tableCellStyle}>{user.firstName}</td>
            <td className={tableCellStyle}>{user.nick}</td>
            <td className={tableCellStyle}>{user.lastName}</td>
            <td className={tableCellStyle}>{user.acceptanceYear}</td>
            <td>
              <Link to={"/users/" + user.id}>Details</Link>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

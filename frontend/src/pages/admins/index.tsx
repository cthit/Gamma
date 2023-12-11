import { useAdminsLoaderData } from "./loader";
import { Link, useRevalidator } from "react-router-dom";
import { GammaClient } from "../../client/gamma";

const tableCellStyle = "border border-slate-300 p-1";

export const AdminsPage = () => {
  const { admins, users } = useAdminsLoaderData();
  const revalidator = useRevalidator();

  const setAdmin =
    (userId: string) => (e: React.ChangeEvent<HTMLInputElement>) => {
      GammaClient.instance()
        .admins.setAdmin(userId, e.target.checked)
        .then(() => {
          revalidator.revalidate();
        });
    };

  return (
    <table
      className={
        "m-auto w-full table-fixed border-collapse border border-slate-400"
      }
    >
      <caption className={"mb-4 font-bold"}>Set admins</caption>
      <thead>
        <tr>
          <th className={tableCellStyle + " text-left"}>First name</th>
          <th className={tableCellStyle + " text-left"}>Nick</th>
          <th className={tableCellStyle + " text-left"}>Last name</th>
          <th className={tableCellStyle + " text-left"}>Is Admin</th>
          <th>Details</th>
        </tr>
      </thead>
      <tbody>
        {users.map((user) => (
          <tr key={user.id} className={tableCellStyle}>
            <td className={tableCellStyle}>{user.firstName}</td>
            <td className={tableCellStyle}>{user.nick}</td>
            <td className={tableCellStyle}>{user.lastName}</td>
            <td className={tableCellStyle}>
              <input
                type={"checkbox"}
                checked={admins.includes(user.id)}
                onChange={setAdmin(user.id)}
              />
            </td>
            <td>
              <Link to={"/users/" + user.id}>Details</Link>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

import { useGdprTrainedLoaderData } from "./loader";
import { Link, useRevalidator } from "react-router-dom";
import { GammaClient } from "../../client/gamma-client";

const tableCellStyle = "border border-slate-300 p-1";

export const GdprTrainedPage = () => {
  const { users, gdprTrained } = useGdprTrainedLoaderData();
  const revalidator = useRevalidator();

  const setGdprTrained =
    (userId: string) => (e: React.ChangeEvent<HTMLInputElement>) => {
      GammaClient.getInstance()
        .gdpr.setGdprTrained(userId, e.target.checked)
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
      <caption className={"mb-4 font-bold"}>Set gdpr trained</caption>
      <thead>
        <tr>
          <th className={tableCellStyle + " text-left"}>First name</th>
          <th className={tableCellStyle + " text-left"}>Nick</th>
          <th className={tableCellStyle + " text-left"}>Last name</th>
          <th className={tableCellStyle + " text-left"}>Has gdpr trained</th>
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
                checked={gdprTrained.includes(user.id)}
                onChange={setGdprTrained(user.id)}
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

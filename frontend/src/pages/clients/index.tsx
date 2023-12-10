import { useClientsLoaderData } from "./loader";
import { Link } from "react-router-dom";

const tableCellStyle = "border border-slate-300 p-1";

export const ClientsPage = () => {
  const clients = useClientsLoaderData();

  return (
    <table
      className={
        "m-auto w-full table-fixed border-collapse border border-slate-400"
      }
    >
      <thead>
        <tr>
          <th className={tableCellStyle + " text-left"}>Name</th>
          <th className={tableCellStyle + " text-left"}>Details</th>
        </tr>
      </thead>
      <tbody>
        {clients.map((client) => (
          <tr key={client.clientUid}>
            <td className={tableCellStyle}>{client.prettyName}</td>
            <td className={tableCellStyle}>
              <Link to={"/clients/" + client.clientUid}>Details</Link>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

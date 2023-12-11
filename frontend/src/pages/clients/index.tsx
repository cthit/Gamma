import { useClientsLoaderData } from "./loader";
import { Link, useRevalidator } from "react-router-dom";
import { GammaClient } from "../../client/gamma";

const tableCellStyle = "border border-slate-300 p-1";

export const ClientsPage = () => {
  const clients = useClientsLoaderData();
  const revalidator = useRevalidator();

  const deleteClient = (clientUid: string) => () => {
    GammaClient.instance()
      .clients.deleteClient(clientUid)
      .then(() => revalidator.revalidate());
  };

  return (
    <div className={"flex flex-col gap-4"}>
      <Link to={"/clients/create"}>Create new client</Link>
      <table
        className={
          "m-auto w-full table-fixed border-collapse border border-slate-400"
        }
      >
        <thead>
          <tr>
            <th className={tableCellStyle + " text-left"}>Name</th>
            <th className={tableCellStyle + " text-left"}>Has restrictions</th>
            <th className={tableCellStyle + " text-left"}>Details</th>
            <th className={tableCellStyle + " text-left"}>Delete</th>
          </tr>
        </thead>
        <tbody>
          {clients.map((client) => (
            <tr key={client.clientUid}>
              <td className={tableCellStyle}>{client.prettyName}</td>
              <td className={tableCellStyle}>
                {client.restriction === null ? "No" : "Yes"}
              </td>
              <td className={tableCellStyle}>
                <Link to={"/clients/" + client.clientUid}>Details</Link>
              </td>
              <td className={tableCellStyle}>
                <button onClick={deleteClient(client.clientUid)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

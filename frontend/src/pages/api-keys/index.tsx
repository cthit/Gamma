import { useApiKeysLoaderData } from "./loader";
import { Link, useRevalidator } from "react-router-dom";
import { GammaClient } from "../../client/gamma";

const tableCellStyle = "border border-slate-300 p-1";

export const ApiKeysPage = () => {
  const { apiKeys } = useApiKeysLoaderData();
  const revalidator = useRevalidator();

  const deleteApiKey = (apiKeyId: string) => () => {
    GammaClient.instance()
      .apiKeys.deleteApiKey(apiKeyId)
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
      <thead>
        <tr>
          <th className={tableCellStyle + " text-left"}>Name</th>
          <th className={tableCellStyle + " text-left"}>Type</th>
          <th className={tableCellStyle + " text-left"}>Details</th>
          <th className={tableCellStyle + " text-left"}>Delete</th>
        </tr>
      </thead>
      <tbody>
        {apiKeys.map((apiKey) => (
          <tr key={apiKey.id}>
            <td className={tableCellStyle}>{apiKey.prettyName}</td>
            <td className={tableCellStyle}>{apiKey.keyType}</td>
            <td className={tableCellStyle}>
              <Link to={"/api-keys/" + apiKey.id}>Details</Link>
            </td>
            <td className={tableCellStyle}>
              <button onClick={deleteApiKey(apiKey.id)}>Delete</button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

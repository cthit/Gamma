import { useActivationCodesLoaderData } from "./loader";

const tableCellStyle = "border border-slate-300 p-1";

export const ActivationCodesPage = () => {
  const activationCodes = useActivationCodesLoaderData();

  console.log(activationCodes);

  return (
    <table
      className={
        "m-auto w-full table-fixed border-collapse border border-slate-400"
      }
    >
      <thead>
        <tr>
          <th className={tableCellStyle + " text-left"}>Cid</th>
          <th className={tableCellStyle + " text-left"}>Token</th>
          <th className={tableCellStyle + " text-left"}>Created at</th>
        </tr>
      </thead>
      <tbody>
        {activationCodes.map((activationCode) => (
          <tr key={activationCode.cid}>
            <td>{activationCode.cid}</td>
            <td>{activationCode.token}</td>
            <td>{activationCode.createdAt}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

import { useAllowListLoaderData } from "./loader";

const tableCellStyle = "border border-slate-300 p-1";

export const AllowListPage = () => {
  const allowList = useAllowListLoaderData();

  return (
    <table
      className={
        "m-auto w-full table-fixed border-collapse border border-slate-400"
      }
    >
      <thead>
        <tr>
          <th className={tableCellStyle + " text-left"}>Allowed cid</th>
        </tr>
      </thead>
      <tbody>
        {allowList.map((allowed) => (
          <tr key={allowed}>
            <td>{allowed}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

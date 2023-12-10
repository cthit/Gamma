import { useSuperGroupsLoaderData } from "./loader";
import { Link } from "react-router-dom";

const tableCellStyle = "border border-slate-300 p-1";

export const SuperGroupsPage = () => {
  const superGroups = useSuperGroupsLoaderData();

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
        </tr>
      </thead>
      <tbody>
        {superGroups.map((superGroup) => (
          <tr key={superGroup.id}>
            <td className={tableCellStyle}>{superGroup.prettyName}</td>
            <td className={tableCellStyle}>
              <Link to={"/types/" + superGroup.type}>{superGroup.type}</Link>
            </td>
            <td className={tableCellStyle}>
              <Link to={"/super-groups/" + superGroup.id}>Details</Link>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

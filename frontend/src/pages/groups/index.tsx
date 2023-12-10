import { useGroupsLoaderData } from "./loader";
import { Link } from "react-router-dom";

const tableCellStyle = "border border-slate-300 p-1";

export const GroupsPage = () => {
  const groups = useGroupsLoaderData();

  return (
    <table
      className={
        "m-auto w-full table-fixed border-collapse border border-slate-400"
      }
    >
      <thead>
        <tr>
          <th className={tableCellStyle + " text-left"}>Name</th>
          <th className={tableCellStyle + " text-left"}>Super group</th>
          <th className={tableCellStyle + " text-left"}>Details</th>
        </tr>
      </thead>
      <tbody>
        {groups.map((group) => (
          <tr key={group.id}>
            <td className={tableCellStyle}>{group.prettyName}</td>
            <td className={tableCellStyle}>
              <Link to={"/super-groups/" + group.superGroup.id}>
                {group.superGroup.prettyName}
              </Link>
            </td>
            <td className={tableCellStyle}>
              <Link to={"/groups/" + group.id}>Details</Link>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

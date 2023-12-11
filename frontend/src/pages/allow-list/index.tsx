import { useAllowListLoaderData } from "./loader";
import * as z from "zod";
import { useRevalidator } from "react-router-dom";
import { GammaClient } from "../../client/gamma";

const tableCellStyle = "border border-slate-300 p-1";

const allowValidation = z
  .object({
    cid: z.string(),
  })
  .strict();

export const AllowListPage = () => {
  const allowList = useAllowListLoaderData();
  const revalidator = useRevalidator();

  const onSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const form = Object.fromEntries(new FormData(e.currentTarget));

    const validatedForm = allowValidation.parse(form);
    GammaClient.instance()
      .allowList.allow(validatedForm.cid)
      .then(() => revalidator.revalidate());
  };

  const removeAllowedCid = (cid: string) => () => {
    GammaClient.instance()
      .allowList.remove(cid)
      .then(() => revalidator.revalidate());
  };

  return (
    <div className={"flex flex-col gap-4 w-1/2 m-auto"}>
      <table
        className={"m-auto table-fixed border-collapse border border-slate-400"}
      >
        <thead>
          <tr>
            <th className={tableCellStyle + " text-left"}>Allowed cid</th>
            <th className={tableCellStyle + " text-left"}>Remove</th>
          </tr>
        </thead>
        <tbody>
          {allowList.map((allowed) => (
            <tr key={allowed}>
              <td>{allowed}</td>
              <td>
                <button onClick={removeAllowedCid(allowed)}>Remove</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className={"outline flex flex-col gap-2"}>
        <form className={"contents"} onSubmit={onSubmit}>
          <label htmlFor={"allowCid"}>Allow new cid</label>
          <input
            className={"outline"}
            type="text"
            id={"allowCid"}
            name="cid"
            placeholder={"Cid"}
          />
          <button type={"submit"}>Allow</button>
        </form>
      </div>
    </div>
  );
};

import * as z from "zod";
import { GammaClient } from "../../client/gamma";
import { useNavigate } from "react-router-dom";

const enterCidValidation = z
  .object({
    cid: z.string(),
  })
  .strict();

export const EnterCidPage = () => {
  const navigate = useNavigate();

  const onSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const form = Object.fromEntries(new FormData(e.currentTarget));

    const validatedForm = enterCidValidation.parse(form);
    GammaClient.instance()
      .createAccount.enterCid(validatedForm.cid)
      .then(() => {
        navigate("/create-account/email-sent");
      });
  };

  return (
    <div className={"mx-auto mt-4 w-fit flex flex-col gap-4"}>
      <form className={"contents"} onSubmit={onSubmit}>
        <input className={"outline"} type="text" name="cid" />
        <button type={"submit"}>Send cid</button>
      </form>
    </div>
  );
};

import { GammaClient } from "../../client/gamma-client";
import * as z from "zod";

const inputNewAccountValidation = z
  .object({
    cid: z.string(),
    code: z.string(),
    password: z.string(),
    confirmPassword: z.string(),
    nick: z.string(),
    firstName: z.string(),
    lastName: z.string(),
    email: z.string(),
    acceptanceYear: z.coerce.number(),
    userAgreement: z.coerce.boolean().refine((value) => value),
    language: z.enum(["SV", "EN"]),
  })
  .strict()
  .refine(({ password, confirmPassword }) => password === confirmPassword);

export const InputNewAccountPage = () => {
  const onSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const form = Object.fromEntries(new FormData(e.currentTarget));

    const validatedForm = inputNewAccountValidation.parse(form);
    GammaClient.getInstance()
      .createAccount.createAccount(validatedForm)
      .then(() => {
        window.location.href = "http://gamma:8081/api/login";
      });
  };

  return (
    <div className={"mx-auto mt-4 w-fit flex flex-col gap-4"}>
      <form className={"contents"} onSubmit={onSubmit}>
        <input
          placeholder={"Cid"}
          className={"outline"}
          type="text"
          name="cid"
        />
        <input
          placeholder={"Code"}
          className={"outline"}
          type="text"
          name="code"
        />
        <input
          placeholder={"Password"}
          className={"outline"}
          type="password"
          name="password"
        />
        <input
          placeholder={"Confirm password"}
          className={"outline"}
          type="password"
          name="confirmPassword"
        />
        <input
          placeholder={"Nick"}
          className={"outline"}
          type="text"
          name="nick"
        />
        <input
          placeholder={"First name"}
          className={"outline"}
          type="nick"
          name="firstName"
        />
        <input
          placeholder={"Last name"}
          className={"outline"}
          type="nick"
          name="lastName"
        />
        <input
          placeholder={"Email"}
          className={"outline"}
          type="nick"
          name="email"
        />
        <input
          placeholder={"Acceptance year"}
          className={"outline"}
          type="nick"
          name="acceptanceYear"
        />
        <label htmlFor={"userAgreement"}>
          Do you accept the user agreement?
        </label>
        <input id={"userAgreement"} type={"checkbox"} name={"userAgreement"} />
        <select name="language">
          <option value={"EN"}>Prefer English</option>
          <option value={"SV"}>Prefer Swedish</option>
        </select>
        <button type={"submit"}>Create account</button>
      </form>
    </div>
  );
};

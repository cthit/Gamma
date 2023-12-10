import { Link } from "react-router-dom";

export const EmailSentPage = () => {
  return (
    <div className={"mx-auto mt-4 w-fit flex flex-col gap-4"}>
      <p>
        If you have not received an email within a few minutes, you may have
        entered the wrong cid. If you're sure that you have written the correct
        cid and you still haven't received an email please contact digIT at
        digit@chalmers.it
      </p>
      <Link to={"/create-account"}>I have not received a code</Link>
      <Link to={"/create-account/input"}>I have received a code</Link>
    </div>
  );
};

import React from "react";
import Button from "@material-ui/core/Button";
import TextField from "@material-ui/core/TextField";

export const InputCidScreen = () => (
  <div>
    <TextField label="cid" />
    <Button variant="contained" color="primary">
      Skicka cid
    </Button>
  </div>
);

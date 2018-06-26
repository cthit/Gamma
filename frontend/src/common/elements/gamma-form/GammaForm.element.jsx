import React from "react";

import { Formik, Form } from "formik";

const GammaForm = ({ initialValues, onSubmit, render }) => (
  <Formik
    initialValues={{ ...initialValues }}
    onSubmit={onSubmit}
    render={() => <Form>{render()}</Form>}
  />
);

export default GammaForm;

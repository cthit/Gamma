import React from "react";
import { shallow } from "enzyme";
import loadTranslations from "./translations.loader";

const localize = {
  languages: [
    { name: "English", code: "en", active: true },
    { name: "Swedish", code: "sv", active: false }
  ],
  translations: {
    "Common.Back": ["Back", "Tillbaka"],
    "Common.FieldRequired": [
      "This field is required",
      "Det här fältet är obligatoriskt"
    ],
    "CreateAccount.SendCid": ["Send cid", "Skicka cid"],
    "CreateAccount.GetActivationCode": [
      "Get activation code",
      "Hämta aktiveringskod"
    ],
    "CreateAccount.CreateAccount": ["Create account", "Skapa konto"],
    "CreateAccount.View.CreationOfAccountFinished": {},
    "CreateAccount.View.EmailHasBeenSent.AnEmailShouldBeSent": [
      "An email should be sent to your student email",
      "Ett mail ska ha skickats till din studentmail"
    ],
    "CreateAccount.View.EmailHasBeenSent.AnEmailShouldBeSentDescription": [
      "If you have not recieved an email within a few minutes, you may have entered the wrong cid. If you're sure that you have written the correct cid and you still haven't recieved an email please contact digIT at digit@chalmers.it",
      "Om du inte får ett mail på några minuter kan du ha råkat skriva fel cid. Om du är säker att du skriver rätt med fortfarande inte får ett mail kan det antingen bero på att mailet har hamnat i skräppost, eller så är du inte inlagd i digITs system. I det senare fallet, var vänligen och skicka ett mail till digit@chalmers.it."
    ],
    "CreateAccount.View.EmailHasBeenSent.HaveRecievedACode": [
      "I have recieved a code",
      "Jag har fått en kod"
    ],
    "CreateAccount.View.InputCid.EnterYourCid": [
      "Enter your cid",
      "Skriv in ditt cid"
    ],
    "CreateAccount.View.InputCid.EnterYourCidDescription": [
      "We will send an email to your student email to confirm your identity.",
      "Vi kommer skicka ett mail till din studentmail för att bekräfta din identitet."
    ],
    "CreateAccount.View.InputCid.SendCid": ["Send cid", "Skicka cid"],
    "CreateAccount.View.InputCid.Cid": ["Cid", "Cid"],
    "CreateAccount.View.InputDataAndCode.CompleteCreation": [
      "Complete creation of your account",
      "Slutför registreringen av konto"
    ],
    "CreateAccount.View.InputDataAndCode.CompleteCreationDescription": [
      "Enter your cid and your code that you recieved from your student email along with the following information",
      "Skriv in ditt cid och koden du fick på din skolmail samt följande information"
    ],
    "CreateAccount.View.InputDataAndCode.YourCid": ["Your cid", "Ditt cid"],
    "CreateAccount.View.InputDataAndCode.CodeFromYourStudentEmail": [
      "Code from your student email",
      "Kod från din studentmail"
    ],
    "CreateAccount.View.InputDataAndCode.Nick": ["Nick", "Nick"],
    "CreateAccount.View.InputDataAndCode.Password": ["Password", "Lösenord"],
    "CreateAccount.View.InputDataAndCode.ConfirmPassword": [
      "Confirm password",
      "Bekräfta lösenord"
    ],
    "CreateAccount.View.InputDataAndCode.FirstName": ["First name", "Förnamn"],
    "CreateAccount.View.InputDataAndCode.LastName": ["Last name", "Efternamn"],
    "CreateAccount.View.InputDataAndCode.WhichYearDidYouStart": [
      "Which year did you start at IT?",
      "Vilket år började du på IT?"
    ],
    "CreateAccount.View.InputDataAndCode.AcceptUserAgreement": [
      "I accept the user agreements",
      "Jag accepterar användaravtalet"
    ],
    "CreateAccount.View.InputDataAndCode.CreateAccount": [
      "Create account",
      "Skapa konto"
    ],
    "CreateAccount.View.InputDataAndCode.PasswordsDoNotMatch": [
      "Passwords do not match",
      "Lösenorden matchar inte"
    ],
    "demo.asdf": ["fdas", "loool"]
  },
  options: {
    renderInnerHtml: true,
    ignoreTranslateChildren: false,
    defaultLanguage: "en"
  }
};

const translations = {
  SendCid: ["Send cid", "Skicka cid"],
  GetActivationCode: ["Get activation code", "Hämta aktiveringskod"],
  CreateAccount: ["Create account", "Skapa konto"]
};

const baseUrl = "CreateAccount.";

const expected = {
  SendCid: "Send cid",
  GetActivationCode: "Get activation code",
  CreateAccount: "Create account",
  Back: "Back",
  FieldRequired: "This field is required"
};

describe("loadTranslations", () => {
  test("loadTranslations test", () => {
    expect(loadTranslations(localize, translations, baseUrl)).toEqual(expected);
  });
});

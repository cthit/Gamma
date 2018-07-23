import React from "react";
import styled from "styled-components";
import { Add } from "@material-ui/icons";

import { Heading, Title, Subtitle } from "../../common-ui/text";

import GammaTable from "../../common/temp/GammaTable";

class Demo extends React.Component {
  state = {
    selected: []
  };

  render() {
    const { selected } = this.state;

    console.log("Selected: ");
    console.log(selected);

    return (
      <div>
        <StyledGammaTable
          startOrderBy="firstName"
          headerTexts={{
            firstName: "First name",
            nick: "Nick",
            lastName: "Last name",
            id: "Cid",
            acceptanceYear: "Acceptance Year"
          }}
          data={[
            {
              id: "svensven",
              nick: "Portals",
              firstName: "Sven",
              lastName: "Svensson",
              acceptanceYear: 2018
            },
            {
              id: "andand",
              nick: "Gurr",
              firstName: "Anders",
              lastName: "Andersson",
              acceptanceYear: 2015
            },
            {
              id: "glasss",
              nick: "NeonSky",
              firstName: "Glass",
              lastName: "Glasson",
              acceptanceYear: 2001
            },
            {
              id: "icesson",
              nick: "Iller",
              firstName: "Ice",
              lastName: "Icesson",
              acceptanceYear: 2003
            },
            {
              id: "pajpaj",
              nick: "Pi",
              firstName: "Paj",
              lastName: "Pajsson",
              acceptanceYear: 2005
            },
            {
              id: "asdfa",
              nick: "LP",
              firstName: "Asdf",
              lastName: "Asdfsson",
              acceptanceYear: 2007
            },
            {
              id: "glads",
              nick: "Vidde",
              firstName: "Glad",
              lastName: "Gladsson",
              acceptanceYear: 2009
            },
            {
              id: "hmhmm",
              nick: "Jerge",
              firstName: "Hmm",
              lastName: "Hmmsson",
              acceptanceYear: 2011
            }
          ]}
          selected={selected}
          onSelectedUpdated={newSelected =>
            this.setState({
              selected: newSelected
            })
          }
        />
      </div>
    );
  }
}

const StyledGammaTable = styled(GammaTable)``;

export default Demo;

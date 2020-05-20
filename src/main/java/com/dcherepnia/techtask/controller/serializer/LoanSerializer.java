package com.dcherepnia.techtask.controller.serializer;

import com.dcherepnia.techtask.domain.Loan;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

class LoanSerializer extends StdSerializer<Loan> {

    public LoanSerializer() {
        this(null);
    }

    public LoanSerializer(Class<Loan> t) {
        super(t);
    }

    @Override
    public void serialize(Loan loan, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", loan.getId());
        jsonGenerator.writeNumberField("amount", loan.getAmount());
        jsonGenerator.writeStringField("term", loan.getTerm().toString());
        jsonGenerator.writeStringField("customer", loan.getCustomer().toString());
        jsonGenerator.writeEndObject();
    }
}

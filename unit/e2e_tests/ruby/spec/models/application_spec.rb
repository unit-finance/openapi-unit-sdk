# frozen_string_literal: true

require 'openapi_client'
require_relative '../spec_helper'
require 'json'
require 'date'

RSpec.describe 'Application' do
  before do
    configure_tests
  end

  describe 'test attribute "id"' do
    let(:api_instance) { OpenapiClient::UnitApi.new(OpenapiClient::ApiClient.new(configuration)) }

    it 'should create an application' do
      request = { data: OpenapiClient::CreateIndividualApplication.new(type: 'individualApplication',
                                                                       attributes: OpenapiClient::CreateIndividualApplicationAttributes.new(
                                                                         ssn: '123456789', full_name: OpenapiClient::FullName.new({ first: 'John', last: 'Kenn' }), date_of_birth: Date.new(1989, 2, 1), address: ADDRESS, email: EMAIL, phone: PHONE,
                                                                         occupation: 'ArchitectOrEngineer', annual_income: 'UpTo10k', source_of_income: 'EmploymentOrPayrollIncome'
                                                                       )).to_hash }
      response = api_instance.create_application(request)
      expect(response.data.type).to eq('individualApplication')
    end

    it 'should upload a document for the application' do
      request = api_instance.upload_application_document_file('2624618', '541996', get_document_contents)

      expect(request.data.type).to eq('document')
    end
  end
end